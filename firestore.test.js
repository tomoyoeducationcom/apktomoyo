const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const PARENT_A_UID = "parent_a_uid_123";
const PARENT_B_UID = "parent_b_uid_456";
const ADMIN_UID = "Tlqf2dNHYCRuYmxgEbHFa783iDs1";
const ADMIN_EMAIL = "tomoyoeducation.com@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function parentAContext() {
  return testEnv
    .authenticatedContext(PARENT_A_UID, {
      email: "ibu.sari@gmail.com",
      email_verified: true,
    })
    .firestore();
}

function parentBContext() {
  return testEnv
    .authenticatedContext(PARENT_B_UID, {
      email: "bapak.budi@gmail.com",
      email_verified: true,
    })
    .firestore();
}

function unverifiedContext() {
  return testEnv
    .authenticatedContext(PARENT_A_UID, {
      email: "unverified@gmail.com",
      email_verified: false,
    })
    .firestore();
}

function adminContext() {
  return testEnv
    .authenticatedContext(ADMIN_UID, {
      email: ADMIN_EMAIL,
      email_verified: true,
    })
    .firestore();
}

test("Unauthenticated and unverified email users are denied access to parents, students, and payments", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const unverifiedDb = unverifiedContext();
  const now = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("parents").doc(PARENT_A_UID).get());
  await assertFails(
    unauthDb
      .collection("students")
      .where("parentId", "==", PARENT_A_UID)
      .get()
  );
  await assertFails(
    unverifiedDb
      .collection("parents")
      .doc(PARENT_A_UID)
      .set({
        uid: PARENT_A_UID,
        displayName: "Ibu Sari",
        email: "unverified@gmail.com",
        createdAt: now,
        updatedAt: now,
      })
  );
});

test("Parent A can create and update own profile, cannot read or overwrite Parent B profile", async () => {
  const dbA = parentAContext();
  const dbB = parentBContext();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    dbA.collection("parents").doc(PARENT_A_UID).set({
      uid: PARENT_A_UID,
      displayName: "Ibu Sari",
      email: "ibu.sari@gmail.com",
      phone: "081234567890",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    dbA.collection("parents").doc(PARENT_A_UID).update({
      displayName: "Ibu Sari Indramayu",
      phone: "081299887766",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable email / createdAt tampering
  await assertFails(
    dbA.collection("parents").doc(PARENT_A_UID).update({
      email: "spoof@gmail.com",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Cross-parent isolation
  await assertFails(dbB.collection("parents").doc(PARENT_A_UID).get());
  await assertFails(dbB.collection("parents").get());
});

test("Parent A can register child (pending/menunggu), query own students, edit pending child, and cannot elevate status or view Parent B child", async () => {
  const dbA = parentAContext();
  const dbB = parentBContext();
  const created = new Date(Date.now() - 10000);

  const validStudentPayload = {
    parentId: PARENT_A_UID,
    parentName: "Ibu Sari",
    parentEmail: "ibu.sari@gmail.com",
    parentPhone: "081234567890",
    childName: "Raka Pratama",
    childPhotoUrl: "https://res.cloudinary.com/fgxdx2hq/image/upload/v1/tomoyo/raka.jpg",
    childGender: "Laki-laki",
    gradeLevel: "Kelas 3 SD",
    packageId: "paket-2",
    packageName: "PAKET 2: Kelas 2 - Kelas 5 (Mata pelajaran)",
    branchId: "indramayu-kota",
    branchName: "TOMOYO EDUCATION INDRAMAYU KOTA (PUSAT)",
    registrationChannel: "online",
    billingAmount: 250000,
    registeredAtIso: created.toISOString(),
    nextDueAtIso: new Date(created.getTime() + 30 * 86400000).toISOString(),
    status: "pending",
    academicStatus: "menunggu",
    createdAt: created,
    updatedAt: created,
  };

  await assertSucceeds(
    dbA.collection("students").doc("stu_raka").set(validStudentPayload)
  );

  // Filtered query succeeds, unfiltered query fails
  await assertSucceeds(
    dbA.collection("students").where("parentId", "==", PARENT_A_UID).get()
  );
  await assertFails(dbA.collection("students").get());

  // Parent A cannot inject @DocumentId 'id' or pre-approve self
  await assertFails(
    dbA.collection("students").doc("stu_spoof").set({
      ...validStudentPayload,
      id: "stu_spoof",
    })
  );
  await assertFails(
    dbA.collection("students").doc("stu_self_approve").set({
      ...validStudentPayload,
      status: "approved",
      academicStatus: "aktif",
    })
  );

  // Parent A can update pending child allowed fields
  await assertSucceeds(
    dbA.collection("students").doc("stu_raka").update({
      childName: "Raka Pratama Putra",
      gradeLevel: "Kelas 4 SD",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Parent A cannot alter status, academicStatus, or parentId
  await assertFails(
    dbA.collection("students").doc("stu_raka").update({
      status: "approved",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    dbA.collection("students").doc("stu_raka").update({
      parentId: PARENT_B_UID,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Parent B cannot read or update Parent A's child
  await assertFails(dbB.collection("students").doc("stu_raka").get());
  await assertFails(
    dbB.collection("students").where("parentId", "==", PARENT_A_UID).get()
  );
  await assertFails(
    dbB.collection("students").doc("stu_raka").update({
      childName: "Hijacked",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Payments are strictly read-only for parents and isolated by parentId", async () => {
  const dbAdmin = adminContext();
  const dbA = parentAContext();
  const dbB = parentBContext();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    dbAdmin.collection("payments").doc("pay_1").set({
      studentId: "stu_raka",
      parentId: PARENT_A_UID,
      childName: "Raka Pratama",
      amount: 250000,
      method: "Transfer BCA",
      periodLabel: "November 2026",
      dueDateIso: new Date(created.getTime() + 30 * 86400000).toISOString(),
      status: "lunas",
      recordedBy: ADMIN_UID,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Parent A can read own payment with filtered query
  await assertSucceeds(
    dbA.collection("payments").where("parentId", "==", PARENT_A_UID).get()
  );
  await assertSucceeds(dbA.collection("payments").doc("pay_1").get());

  // Parent A cannot create/update/delete payments manually
  await assertFails(
    dbA.collection("payments").doc("pay_fake").set({
      studentId: "stu_raka",
      parentId: PARENT_A_UID,
      childName: "Raka Pratama",
      amount: 0,
      method: "Manual",
      periodLabel: "Fake",
      status: "lunas",
      recordedBy: PARENT_A_UID,
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertFails(
    dbA.collection("payments").doc("pay_1").update({
      status: "lunas",
      amount: 1,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Parent B cannot read Parent A's payments
  await assertFails(dbB.collection("payments").doc("pay_1").get());
  await assertFails(
    dbB.collection("payments").where("parentId", "==", PARENT_A_UID).get()
  );
});

test("Published CMS collections (packages, branches, announcements, schedules, siteContent) allow published list queries and block parent writes", async () => {
  const dbAdmin = adminContext();
  const dbA = parentAContext();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    dbAdmin.collection("announcements").doc("ann_1").set({
      title: "Jadwal Tryout TKA",
      content: "Tryout TKA dilaksanakan hari Sabtu.",
      dateLabel: "19 Nov 2026",
      isPublished: true,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    dbA.collection("announcements").where("isPublished", "==", true).get()
  );
  await assertFails(dbA.collection("announcements").get());
  await assertFails(
    dbA.collection("announcements").doc("ann_fake").set({
      title: "Fake",
      content: "Fake",
      isPublished: true,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Admin-only collections like quizKeys / aiInstructions / waCampaigns are strictly blocked for parents
  await assertFails(dbA.collection("quizKeys").get());
  await assertFails(dbA.collection("aiInstructions").get());
  await assertFails(dbA.collection("waCampaigns").get());
});
