const {onSchedule} = require("firebase-functions/v2/scheduler");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore, FieldValue, Timestamp} = require("firebase-admin/firestore");

initializeApp();

exports.unmarkCron = onSchedule({
  schedule: "5 18 * * *",
  timeZone: "Asia/Kolkata"
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
      unmarkedRecords: FieldValue.increment(1)
    }, { merge: true });

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
      unmarkedRecords: FieldValue.increment(1)
    }, { merge: true });

    // c) Mark attendance doc processed
    batch.update(doc.ref, { processed: true });
  }

  // 3) Commit the batch
  await batch.commit();
  console.log(`Processed ${snapshot.size} attendance docs.`);
  return null;
});