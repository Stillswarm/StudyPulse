const { onSchedule } = require("firebase-functions/v2/scheduler");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore, FieldValue, Timestamp } = require("firebase-admin/firestore");
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

initializeApp();
const db = getFirestore();

exports.unmarkCron = onSchedule({
  schedule: "30 5 * * *",
  timeZone: "Asia/Kolkata",
}, async (event) => {
  const now = Timestamp.now();
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

  const semSummaryCounts = new Map();
  const courseSummaryCounts = new Map();

  for (const doc of snapshot.docs) {
    const data = doc.data();
    const semKey = `${data.userId}/${data.semesterId}`;
    semSummaryCounts.set(semKey, (semSummaryCounts.get(semKey) || 0) + 1);
    const courseKey = `${data.userId}/${data.semesterId}/${data.courseId}`;
    courseSummaryCounts.set(courseKey, (courseSummaryCounts.get(courseKey) || 0) + 1);
  }

  const batch = db.batch();

  for (const [semKey, count] of semSummaryCounts) {
    const [userId, semesterId] = semKey.split('/');
    const ref = db.collection("users").doc(userId)
      .collection("semesters").doc(semesterId)
      .collection("sem_summaries").doc("sem_summary");
    batch.set(ref, { unmarkedRecords: FieldValue.increment(count) }, { merge: true });
  }

  for (const [courseKey, count] of courseSummaryCounts) {
    const [userId, semesterId, courseId] = courseKey.split('/');
    const ref = db.collection("users").doc(userId)
      .collection("semesters").doc(semesterId)
      .collection("courses").doc(courseId)
      .collection("course_summaries").doc("course_summary");
    batch.set(ref, { unmarkedRecords: FieldValue.increment(count) }, { merge: true });
  }

  for (const doc of snapshot.docs) {
    batch.update(doc.ref, { processed: true });
  }

  await batch.commit();
  console.log(`Processed ${snapshot.size} attendance docs.`);
  return null;
});

exports.notifyFeedback = functions
  .runWith({
    secrets: ['SMTP_USER', 'SMTP_PASS', 'TEAM_EMAIL'],
    memory: '256MB',
    timeoutSeconds: 60
  })
  .region('asia-south1')
  .firestore
  .document('feedback/{docId}')
  .onCreate(async (snap, ctx) => {
    console.log('notifyFeedback triggered — docId =', ctx.params.docId);
    const data = snap.data() || {};
    const { userId, message, createdAt } = data;
    const timestamp = createdAt?.toDate?.().toISOString() || new Date().toISOString();

    let userEmail = data.email || null;
    if (!userEmail && userId) {
      try {
        const userRecord = await admin.auth().getUser(userId);
        userEmail = userRecord.email || null;
      } catch (err) {
        console.warn(`Could not fetch auth user for uid=${userId}:`, err?.message || err);
      }
    }

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

exports.onFlashcardCreated = functions
  .region('asia-south1')
  .firestore
  .document("users/{userId}/flashcards/{flashcardId}")
  .onCreate(async (snap, ctx) => {
    const userId = ctx.params.userId;
    const packId = snap.data()?.packId;
    if (!packId) return;

    await db
      .collection(`users/${userId}/flashcardPacks`)
      .doc(packId)
      .update({ fcCount: FieldValue.increment(1) });
  });

exports.onFlashcardDeleted = functions
  .region('asia-south1')
  .firestore
  .document("users/{userId}/flashcards/{flashcardId}")
  .onDelete(async (snap, ctx) => {
    const userId = ctx.params.userId;
    const packId = snap.data()?.packId;
    if (!packId) return;

    await db
      .collection(`users/${userId}/flashcardPacks`)
      .doc(packId)
      .update({ fcCount: FieldValue.increment(-1) });
  });