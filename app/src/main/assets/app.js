/* =========================================================
   PETCARE APP
========================================================= */

const STORAGE = {
    user: "petCareUser",
    session: "petCareSession",
    pet: "petCarePet",
    reminders: "petCareReminders",
    tasks: "petCareTasks",
    theme: "petCareTheme"
};

let reminders =
    JSON.parse(
        localStorage.getItem(STORAGE.reminders) || "[]"
    );

let pet =
    JSON.parse(
        localStorage.getItem(STORAGE.pet) || "null"
    );

let tasks =
    JSON.parse(
        localStorage.getItem(STORAGE.tasks) || "[]"
    );


/* =========================================================
   STARTUP
========================================================= */

document.addEventListener("DOMContentLoaded", () => {

    loadTheme();

    const session =
        JSON.parse(
            localStorage.getItem(STORAGE.session) || "null"
        );

    if (session) {
        showApp();
    } else {
        showAuth();
    }

    renderPet();
    renderReminders();
    renderTasks();

});


/* =========================================================
   AUTH
========================================================= */

function showAuth() {

    document
        .getElementById("authScreen")
        .classList.remove("hidden");

    document
        .getElementById("appScreen")
        .classList.add("hidden");
}


function showApp() {

    document
        .getElementById("authScreen")
        .classList.add("hidden");

    document
        .getElementById("appScreen")
        .classList.remove("hidden");

    const session =
        JSON.parse(
            localStorage.getItem(STORAGE.session) || "{}"
        );

    document.getElementById("welcomeName").textContent =
        "Hi, " + (session.name || "there");

    document.getElementById("profileName").textContent =
        session.name || "User";

    document.getElementById("profileEmail").textContent =
        session.email || "-";

    renderPet();
    renderReminders();
    renderTasks();
}


function showLogin() {

    document
        .getElementById("loginForm")
        .classList.remove("hidden");

    document
        .getElementById("signupForm")
        .classList.add("hidden");

    document
        .getElementById("loginTab")
        .classList.add("active");

    document
        .getElementById("signupTab")
        .classList.remove("active");
}


function showSignup() {

    document
        .getElementById("loginForm")
        .classList.add("hidden");

    document
        .getElementById("signupForm")
        .classList.remove("hidden");

    document
        .getElementById("loginTab")
        .classList.remove("active");

    document
        .getElementById("signupTab")
        .classList.add("active");
}


function signup() {

    const name =
        document
            .getElementById("signupName")
            .value
            .trim();

    const email =
        document
            .getElementById("signupEmail")
            .value
            .trim();

    const password =
        document
            .getElementById("signupPassword")
            .value;

    if (!name || !email || !password) {

        alert("Please fill all fields.");

        return;
    }

    const user = {
        name,
        email,
        password
    };

    localStorage.setItem(
        STORAGE.user,
        JSON.stringify(user)
    );

    localStorage.setItem(
        STORAGE.session,
        JSON.stringify({
            name,
            email
        })
    );

    showApp();
}


function login() {

    const email =
        document
            .getElementById("loginEmail")
            .value
            .trim();

    const password =
        document
            .getElementById("loginPassword")
            .value;

    const user =
        JSON.parse(
            localStorage.getItem(STORAGE.user) || "null"
        );

    if (!user) {

        alert(
            "No account found. Create an account first."
        );

        return;
    }

    if (
        email !== user.email ||
        password !== user.password
    ) {

        alert("Incorrect email or password.");

        return;
    }

    localStorage.setItem(
        STORAGE.session,
        JSON.stringify({
            name: user.name,
            email: user.email
        })
    );

    showApp();
}


function logout() {

    localStorage.removeItem(
        STORAGE.session
    );

    closeProfile();

    showAuth();
}


/* =========================================================
   NAVIGATION
========================================================= */

function openPage(page) {

    const pages = [
        "home",
        "pet",
        "reminders",
        "vet",
        "camera"
    ];

    pages.forEach(name => {

        const element =
            document.getElementById(
                name + "Page"
            );

        if (element) {
            element.classList.remove(
                "active-page"
            );
        }
    });

    const target =
        document.getElementById(
            page + "Page"
        );

    if (target) {
        target.classList.add(
            "active-page"
        );
    }

    document
        .querySelectorAll(".nav-item")
        .forEach(item => {

            item.classList.remove("active");

            if (
                item.dataset.page === page
            ) {
                item.classList.add("active");
            }

        });

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


/* =========================================================
   PROFILE MODAL
========================================================= */

function openProfile() {

    document
        .getElementById("profileModal")
        .classList.remove("hidden");
}


function closeProfile() {

    document
        .getElementById("profileModal")
        .classList.add("hidden");
}


/* =========================================================
   THEME
========================================================= */

function loadTheme() {

    const theme =
        localStorage.getItem(
            STORAGE.theme
        );

    if (theme === "dark") {
        document.body.classList.add("dark");
    }
}


function toggleTheme() {

    document.body.classList.toggle("dark");

    localStorage.setItem(
        STORAGE.theme,
        document.body.classList.contains("dark")
            ? "dark"
            : "light"
    );
}


/* =========================================================
   PET PROFILE
========================================================= */

function savePet() {

    const name =
        document
            .getElementById("petName")
            .value
            .trim();

    const species =
        document
            .getElementById("petSpecies")
            .value
            .trim();

    const breed =
        document
            .getElementById("petBreed")
            .value
            .trim();

    const gender =
        document
            .getElementById("petGender")
            .value;

    const dob =
        document
            .getElementById("petDob")
            .value;

    const weight =
        document
            .getElementById("petWeight")
            .value;

    if (!name) {

        alert("Please enter your pet's name.");

        return;
    }

    pet = {
        name,
        species,
        breed,
        gender,
        dob,
        weight,
        photo: pet ? pet.photo || "" : ""
    };

    localStorage.setItem(
        STORAGE.pet,
        JSON.stringify(pet)
    );

    renderPet();

    alert("Pet profile saved.");
}


function renderPet() {

    if (!pet) {

        document.getElementById(
            "homePetName"
        ).textContent =
            "Add your pet";

        document.getElementById(
            "homePetDetails"
        ).textContent =
            "Create a pet profile";

        return;
    }

    document.getElementById(
        "homePetName"
    ).textContent =
        pet.name;

    const details = [];

    if (pet.species) {
        details.push(pet.species);
    }

    if (pet.breed) {
        details.push(pet.breed);
    }

    if (pet.weight) {
        details.push(
            pet.weight + " kg"
        );
    }

    document.getElementById(
        "homePetDetails"
    ).textContent =
        details.length
            ? details.join(" • ")
            : "Pet profile";

    document.getElementById(
        "petName"
    ).value =
        pet.name || "";

    document.getElementById(
        "petSpecies"
    ).value =
        pet.species || "";

    document.getElementById(
        "petBreed"
    ).value =
        pet.breed || "";

    document.getElementById(
        "petGender"
    ).value =
        pet.gender || "";

    document.getElementById(
        "petDob"
    ).value =
        pet.dob || "";

    document.getElementById(
        "petWeight"
    ).value =
        pet.weight || "";

    if (pet.photo) {

        document.getElementById(
            "petPhotoPreview"
        ).innerHTML =
            `<img src="${pet.photo}" alt="Pet">`;
    }
}


function savePetPhoto(event) {

    const file =
        event.target.files[0];

    if (!file) {
        return;
    }

    const reader =
        new FileReader();

    reader.onload = function () {

        if (!pet) {

            pet = {
                name: "",
                species: "",
                breed: "",
                gender: "",
                dob: "",
                weight: "",
                photo: ""
            };
        }

        pet.photo =
            reader.result;

        localStorage.setItem(
            STORAGE.pet,
            JSON.stringify(pet)
        );

        renderPet();
    };

    reader.readAsDataURL(file);
}


/* =========================================================
   CARE TASKS
========================================================= */

function renderTasks() {

    const container =
        document.getElementById(
            "todayTasks"
        );

    const count =
        document.getElementById(
            "taskCount"
        );

    if (!tasks.length) {

        container.innerHTML = `
            <div class="empty-state glass">
                <p>
                    No care tasks yet.
                </p>
            </div>
        `;

        count.textContent = "0";

        return;
    }

    container.innerHTML = "";

    let completed = 0;

    tasks.forEach((task, index) => {

        if (task.done) {
            completed++;
        }

        const card =
            document.createElement("div");

        card.className =
            "task-card glass" +
            (task.done
                ? " completed"
                : "");

        card.innerHTML = `

            <button
                class="task-check ${
                    task.done ? "done" : ""
                }"
                onclick="toggleTask(${index})"
            >
                ${task.done ? "✓" : ""}
            </button>

            <div class="task-info">

                <strong>
                    ${escapeHTML(task.title)}
                </strong>

                <span>
                    ${escapeHTML(task.time || "Today")}
                </span>

            </div>
        `;

        container.appendChild(card);
    });

    count.textContent =
        String(tasks.length - completed);
}


function toggleTask(index) {

    tasks[index].done =
        !tasks[index].done;

    localStorage.setItem(
        STORAGE.tasks,
        JSON.stringify(tasks)
    );

    renderTasks();
}


/* =========================================================
   REMINDERS
========================================================= */

function addReminder() {

    const title =
        document
            .getElementById("reminderTitle")
            .value
            .trim();

    const date =
        document
            .getElementById("reminderDate")
            .value;

    const time =
        document
            .getElementById("reminderTime")
            .value;

    const repeat =
        document
            .getElementById("reminderRepeat")
            .value;

    if (!title || !date || !time) {

        alert(
            "Enter reminder name, date and time."
        );

        return;
    }

    const when =
        new Date(
            `${date}T${time}`
        );

    if (
        Number.isNaN(
            when.getTime()
        )
    ) {

        alert("Invalid date or time.");

        return;
    }

    if (when <= new Date()) {

        alert(
            "Choose a future date and time."
        );

        return;
    }

    const reminder = {

        id: Date.now(),

        title,

        date,

        time,

        repeat,

        timestamp:
            when.getTime()
    };

    reminders.push(reminder);

    saveReminders();

    scheduleNativeReminder(
        reminder
    );

    document.getElementById(
        "reminderTitle"
    ).value = "";

    renderReminders();

    alert(
        "Reminder set successfully."
    );
}


function saveReminders() {

    localStorage.setItem(
        STORAGE.reminders,
        JSON.stringify(reminders)
    );
}


function renderReminders() {

    const list =
        document.getElementById(
            "remindersList"
        );

    if (!list) {
        return;
    }

    list.innerHTML = "";

    if (!reminders.length) {

        list.innerHTML = `
            <div class="empty-state glass">
                <p>
                    No upcoming reminders.
                </p>
            </div>
        `;

        return;
    }

    reminders
        .sort(
            (a, b) =>
                a.timestamp - b.timestamp
        )
        .forEach(reminder => {

            const card =
                document.createElement("div");

            card.className =
                "reminder-card glass";

            const repeatText =
                reminder.repeat === "daily"
                    ? "Every day"
                    : reminder.repeat === "weekly"
                        ? "Every week"
                        : "Once";

            card.innerHTML = `

                <div class="reminder-icon">

                    <svg viewBox="0 0 24 24">

                        <path
                            d="M18 8
                            C18 5 16 3 12 3
                            C8 3 6 5 6 8
                            V13
                            L4 17
                            H20
                            L18 13Z"
                        />

                        <path
                            d="M10 21
                            H14"
                        />

                    </svg>

                </div>

                <div class="reminder-info">

                    <strong>
                        ${escapeHTML(
                            reminder.title
                        )}
                    </strong>

                    <span>
                        ${formatReminderDate(
                            reminder.timestamp
                        )}
                        •
                        ${repeatText}
                    </span>

                </div>

                <button
                    class="delete-reminder"
                    onclick="deleteReminder(
                        ${reminder.id}
                    )"
                >
                    ×
                </button>
            `;

            list.appendChild(card);
        });
}


function deleteReminder(id) {

    reminders =
        reminders.filter(
            reminder =>
                reminder.id !== id
        );

    saveReminders();

    renderReminders();

    cancelNativeReminder(id);
}


function formatReminderDate(timestamp) {

    return new Date(timestamp)
        .toLocaleString(
            [],
            {
                day: "2-digit",
                month: "short",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            }
        );
}


/* =========================================================
   ANDROID NATIVE BRIDGE
========================================================= */

function scheduleNativeReminder(
    reminder
) {

    if (
        window.AndroidPetCare &&
        AndroidPetCare.scheduleReminder
    ) {

        AndroidPetCare.scheduleReminder(

            String(reminder.id),

            reminder.title,

            String(reminder.timestamp),

            reminder.repeat
        );
    }
}


function cancelNativeReminder(id) {

    if (
        window.AndroidPetCare &&
        AndroidPetCare.cancelReminder
    ) {

        AndroidPetCare.cancelReminder(
            String(id)
        );
    }
}


/* =========================================================
   VET SEARCH
========================================================= */

function findVets() {

    const results =
        document.getElementById(
            "vetResults"
        );

    results.innerHTML = `
        <div class="empty-state glass">
            Finding nearby veterinary hospitals...
        </div>
    `;

    if (!navigator.geolocation) {

        results.innerHTML = `
            <div class="empty-state glass">
                Location is not supported
                on this device.
            </div>
        `;

        return;
    }

    navigator.geolocation.getCurrentPosition(

        position => {

            const lat =
                position.coords.latitude;

            const lon =
                position.coords.longitude;

            /*
             * Opens Google Maps veterinary search
             * near the user's current location.
             */
            const mapsURL =
                `https://www.google.com/maps/search/veterinary+hospital/@${lat},${lon},14z`;

            results.innerHTML = `

                <div class="vet-card glass">

                    <h3>
                        Nearby veterinary hospitals
                    </h3>

                    <p>
                        Search results will open
                        in Google Maps.
                    </p>

                    <div class="vet-actions">

                        <button
                            class="vet-action"
                            onclick="openExternal(
                                '${mapsURL}'
                            )"
                        >
                            Open Maps
                        </button>

                    </div>

                </div>
            `;
        },

        error => {

            results.innerHTML = `
                <div class="empty-state glass">
                    Please allow location access
                    to find nearby vets.
                </div>
            `;
        },

        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 60000
        }
    );
}


/* =========================================================
   EXTERNAL LINKS
========================================================= */

function openExternal(url) {

    window.open(
        url,
        "_blank"
    );
}


/* =========================================================
   SECURITY / HTML ESCAPE
========================================================= */

function escapeHTML(value) {

    return String(value)
        .replace(
            /&/g,
            "&amp;"
        )
        .replace(
            /</g,
            "&lt;"
        )
        .replace(
            />/g,
            "&gt;"
        )
        .replace(
            /"/g,
            "&quot;"
        )
        .replace(
            /'/g,
            "&#039;"
        );
}
