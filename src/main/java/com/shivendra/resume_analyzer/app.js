const API_BASE = "https://resume-analyzer-ui70.onrender.com";

let authToken = null;
let resumeId = null;
let jobId = null;

function showStatus(elementId, message, isError) {
  const el = document.getElementById(elementId);
  el.className = "status " + (isError ? "error" : "success");
  el.textContent = message;
}

async function register() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  try {
    const res = await fetch(`${API_BASE}/api/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || "Registration failed");

    authToken = data.token;
    showStatus("authStatus", "Registered and logged in!", false);
    unlockNextSteps();
  } catch (err) {
    showStatus("authStatus", err.message, true);
  }
}

async function login() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  try {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || "Login failed");

    authToken = data.token;
    showStatus("authStatus", "Logged in!", false);
    unlockNextSteps();
  } catch (err) {
    showStatus("authStatus", err.message, true);
  }
}

function unlockNextSteps() {
  document.getElementById("uploadCard").classList.remove("hidden");
  document.getElementById("jobCard").classList.remove("hidden");
}

async function uploadResume() {
  const fileInput = document.getElementById("resumeFile");
  if (!fileInput.files.length) {
    showStatus("uploadStatus", "Please choose a PDF file first.", true);
    return;
  }

  const formData = new FormData();
  formData.append("file", fileInput.files[0]);

  try {
    const res = await fetch(`${API_BASE}/api/resumes/upload`, {
      method: "POST",
      headers: { "Authorization": "Bearer " + authToken },
      body: formData
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || "Upload failed");

    resumeId = data.id;
    showStatus("uploadStatus", `Resume uploaded! (ID: ${resumeId})`, false);
    checkReadyForMatch();
  } catch (err) {
    showStatus("uploadStatus", err.message, true);
  }
}

async function createJob() {
  const title = document.getElementById("jobTitle").value;
  const description = document.getElementById("jobDescription").value;

  try {
    const res = await fetch(`${API_BASE}/api/jobs`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + authToken
      },
      body: JSON.stringify({ title, description })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || "Failed to save job");

    jobId = data.id;
    showStatus("jobStatus", `Job saved! (ID: ${jobId})`, false);
    checkReadyForMatch();
  } catch (err) {
    showStatus("jobStatus", err.message, true);
  }
}

function checkReadyForMatch() {
  if (resumeId && jobId) {
    document.getElementById("matchCard").classList.remove("hidden");
  }
}

async function runMatch() {
  showStatus("matchStatus", "Analyzing... this may take up to 30 seconds.", false);

  try {
    const res = await fetch(`${API_BASE}/api/match/semantic?resumeId=${resumeId}&jobId=${jobId}`, {
      method: "POST",
      headers: { "Authorization": "Bearer " + authToken }
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || "Match failed");

    // Store results for the results page (Day 15)
    localStorage.setItem("matchScore", data.matchScore);
    localStorage.setItem("resumeId", resumeId);
    localStorage.setItem("jobId", jobId);
    localStorage.setItem("authToken", authToken);

    showStatus("matchStatus", `Match Score: ${data.matchScore}%`, false);
  } catch (err) {
    showStatus("matchStatus", err.message, true);
  }
}