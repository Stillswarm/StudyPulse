const {onSchedule} = require("firebase-functions/v2/scheduler");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore, FieldValue, Timestamp} = require("firebase-admin/firestore");
const functions = require('firebase-functions');
initializeApp();

exports.unmarkCron = onSchedule({
  schedule: "0 0 * * *",
  timeZone: "Asia/Kolkata",
}, async (event) => {
  const db = getFirestore();
  const now = Timestamp.now();

  // 1) Find all unprocessed docs with date <= now
  console.log("Current time:", now.toDate());
  const snapshot = await db
      .collectionGroup("attendance")
      .where("date", "<=", now)
      .where("processed", "==", false)
      .get();

  console.log(`Found ${snapshot.size} unprocessed attendance records`);

  if (snapshot.empty) {
    console.log("No new attendance to process today.");
    return null;
  }

  // 2) Batch update: increment summary and mark each processed
  const batch = db.batch();

  for (const doc of snapshot.docs) {
    const data = doc.data();
    console.log(`Processing attendance for user: ${data.userId}, semester: ${data.semesterId}, course: ${data.courseId}`);

    // a) Update semester summary - use set with merge to create if doesn't exist
    const semSummaryRef = db
        .collection("users")
        .doc(data.userId)
        .collection("semesters")
        .doc(data.semesterId)
        .collection("sem_summaries")
        .doc("sem_summary");

    batch.set(semSummaryRef, {
      unmarkedRecords: FieldValue.increment(1),
    }, {merge: true});

    // b) Update course summary - use set with merge to create if doesn't exist
    const courseSummaryRef = db
        .collection("users")
        .doc(data.userId)
        .collection("semesters")
        .doc(data.semesterId)
        .collection("courses")
        .doc(data.courseId)
        .collection("course_summaries")
        .doc("course_summary");

    batch.set(courseSummaryRef, {
      unmarkedRecords: FieldValue.increment(1),
    }, {merge: true});

    // c) Mark attendance doc processed
    batch.update(doc.ref, {processed: true});
  }

  // 3) Commit the batch
  await batch.commit();
  console.log(`Processed ${snapshot.size} attendance docs.`);
  return null;
});

const nodemailer = require('nodemailer');
exports.notifyFeedback = functions
  .runWith({
    secrets: ['SMTP_USER', 'SMTP_PASS', 'TEAM_EMAIL'],
    memory: '256MB',
    timeoutSeconds: 60
  })
  .region('us-central1')
  .firestore
  .document('feedback/{docId}')
  .onCreate(async (snap, ctx) => {
    console.log('🔥 notifyFeedback triggered — docId =', ctx.params.docId);
    const data = snap.data() || {};
    console.log(' 🔍 Payload:', data);

    const { userId, message, createdAt } = data;
    const timestamp = createdAt?.toDate?.().toISOString() || new Date().toISOString();

    // Try to get an email from the document first
    let userEmail = data.email || null;

    // If we don't have an email in the document but do have a userId, try to resolve via Firebase Auth
    if (!userEmail && userId) {
      try {
        const userRecord = await admin.auth().getUser(userId);
        userEmail = userRecord.email || null;
        console.log(`Resolved email for uid=${userId}:`, userEmail);
      } catch (err) {
        console.warn(`Could not fetch auth user for uid=${userId}:`, err?.message || err);
      }
    }

    // Access secrets via environment variables
    const smtpUser = process.env.SMTP_USER;
    const smtpPass = process.env.SMTP_PASS;
    const teamEmail = process.env.TEAM_EMAIL;

    const transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: { user: smtpUser, pass: smtpPass }
    });

    const identity = userEmail || userId || 'Unknown user';
    const mailOptions = {
      from: `"My App Feedback" <${smtpUser}>`,
      to: teamEmail,
      subject: `Feedback from ${identity}`,
      text: `At ${timestamp}, ${identity} wrote:\n\n${message}`
    };

    try {
      await transporter.sendMail(mailOptions);
      console.log('Feedback email sent for', ctx.params.docId);
    } catch (err) {
      console.error('Error sending feedback email:', err);
    }

    return null;
  });