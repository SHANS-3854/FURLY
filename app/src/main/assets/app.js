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

document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadTheme();

        const session =
            JSON.parse(
                localStorage.getItem(
                    STORAGE.session
                ) || "null"
            );

        if (session) {
            showApp();
        } else {
            showAuth();
        }

        renderPet();
        renderReminders();
        renderTasks();
    }
);


/* =========================================================
   AUTH
========================================================= */

function showAuth() {

    const authScreen =
        document.getElementById(
            "authScreen"
        );

    const appScreen =
        document.getElementById(
            "appScreen"
        );

    if (authScreen) {
        authScreen.classList.remove(
            "hidden"
        );
    }

    if (appScreen) {
        appScreen.classList.add(
            "hidden"
        );
    }
}


function showApp() {

    const authScreen =
        document.getElementById(
            "authScreen"
        );

    const appScreen =
        document.getElementById(
            "appScreen"
        );

    if (authScreen) {
        authScreen.classList.add(
            "hidden"
        );
    }

    if (appScreen) {
        appScreen.classList.remove(
            "hidden"
        );
    }

    const session =
        JSON.parse(
            localStorage.getItem(
                STORAGE.session
            ) || "{}"
        );

    const welcomeName =
        document.getElementById(
            "welcomeName"
        );

    const profileName =
        document.getElementById(
            "profileName"
        );

    const profileEmail =
        document.getElementById(
            "profileEmail"
        );

    if (welcomeName) {
        welcomeName.textContent =
            "Hi, " +
            (session.name || "there");
    }

    if (profileName) {
        profileName.textContent =
            session.name || "User";
    }

    if (profileEmail) {
        profileEmail.textContent =
            session.email || "-";
    }

    renderPet();
    renderReminders();
    renderTasks();
}


function showLogin() {

    const loginForm =
        document.getElementById(
            "loginForm"
        );

    const signupForm =
        document.getElementById(
            "signupForm"
        );

    const loginTab =
        document.getElementById(
            "loginTab"
        );

    const signupTab =
        document.getElementById(
            "signupTab"
        );

    if (loginForm) {
        loginForm.classList.remove(
            "hidden"
        );
    }

    if (signupForm) {
        signupForm.classList.add(
            "hidden"
        );
    }

    if (loginTab) {
        loginTab.classList.add(
            "active"
        );
    }

    if (signupTab) {
        signupTab.classList.remove(
            "active"
        );
    }
}


function showSignup() {

    const loginForm =
        document.getElementById(
            "loginForm"
        );

    const signupForm =
        document.getElementById(
            "signupForm"
        );

    const loginTab =
        document.getElementById(
            "loginTab"
        );

    const signupTab =
        document.getElementById(
            "signupTab"
        );

    if (loginForm) {
        loginForm.classList.add(
            "hidden"
        );
    }

    if (signupForm) {
        signupForm.classList.remove(
            "hidden"
        );
    }

    if (loginTab) {
        loginTab.classList.remove(
            "active"
        );
    }

    if (signupTab) {
        signupTab.classList.add(
            "active"
        );
    }
}


/*
 * TEMPORARY LOCAL AUTH
 *
 * This will be replaced with Supabase Auth
 * in the database step.
 */

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
            .trim()
            .toLowerCase();

    const password =
        document
            .getElementById("signupPassword")
            .value;

    if (!name || !email || !password) {

        alert(
            "Please fill all fields."
        );

        return;
    }

    if (password.length < 6) {

        alert(
            "Password must be at least 6 characters."
        );

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
            .trim()
            .toLowerCase();

    const password =
        document
            .getElementById("loginPassword")
            .value;

    const user =
        JSON.parse(
            localStorage.getItem(
                STORAGE.user
            ) || "null"
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

        alert(
            "Incorrect email or password."
        );

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

    pages.forEach(
        name => {

            const element =
                document.getElementById(
                    name + "Page"
                );

            if (element) {

                element.classList.remove(
                    "active-page"
                );
            }
        }
    );

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
        .forEach(
            item => {

                item.classList.remove(
                    "active"
                );

                if (
                    item.dataset.page ===
                    page
                ) {

                    item.classList.add(
                        "active"
                    );
                }
            }
        );

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


/* =========================================================
   PROFILE MODAL
========================================================= */

function openProfile() {

    const modal =
        document.getElementById(
            "profileModal"
        );

    if (modal) {

        modal.classList.remove(
            "hidden"
        );
    }
}


function closeProfile() {

    const modal =
        document.getElementById(
            "profileModal"
        );

    if (modal) {

        modal.classList.add(
            "hidden"
        );
    }
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

        document.body.classList.add(
            "dark"
        );
    }
}


function toggleTheme() {

    document.body.classList.toggle(
        "dark"
    );

    localStorage.setItem(
        STORAGE.theme,
        document.body.classList.contains(
            "dark"
        )
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

        alert(
            "Please enter your pet's name."
        );

        return;
    }

    pet = {

        name,

        species,

        breed,

        gender,

        dob,

        weight,

        photo:
            pet
                ? pet.photo || ""
                : ""
    };

    localStorage.setItem(
        STORAGE.pet,
        JSON.stringify(pet)
    );

    renderPet();

    alert(
        "Pet profile saved."
    );
}


function renderPet() {

    const homePetName =
        document.getElementById(
            "homePetName"
        );

    const homePetDetails =
        document.getElementById(
            "homePetDetails"
        );

    if (!pet) {

        if (homePetName) {

            homePetName.textContent =
                "Add your pet";
        }

        if (homePetDetails) {

            homePetDetails.textContent =
                "Create a pet profile";
        }

        return;
    }

    if (homePetName) {

        homePetName.textContent =
            pet.name;
    }

    const details = [];

    if (pet.species) {

        details.push(
            pet.species
        );
    }

    if (pet.breed) {

        details.push(
            pet.breed
        );
    }

    if (pet.weight) {

        details.push(
            pet.weight + " kg"
        );
    }

    if (homePetDetails) {

        homePetDetails.textContent =
            details.length
                ? details.join(" • ")
                : "Pet profile";
    }

    const petName =
        document.getElementById(
            "petName"
        );

    const petSpecies =
        document.getElementById(
            "petSpecies"
        );

    const petBreed =
        document.getElementById(
            "petBreed"
        );

    const petGender =
        document.getElementById(
            "petGender"
        );

    const petDob =
        document.getElementById(
            "petDob"
        );

    const petWeight =
        document.getElementById(
            "petWeight"
        );

    if (petName) {
        petName.value =
            pet.name || "";
    }

    if (petSpecies) {
        petSpecies.value =
            pet.species || "";
    }

    if (petBreed) {
        petBreed.value =
            pet.breed || "";
    }

    if (petGender) {
        petGender.value =
            pet.gender || "";
    }

    if (petDob) {
        petDob.value =
            pet.dob || "";
    }

    if (petWeight) {
        petWeight.value =
            pet.weight || "";
    }

    const preview =
        document.getElementById(
            "petPhotoPreview"
        );

    if (
        preview &&
        pet.photo
    ) {

        preview.innerHTML =
            `<img src="${escapeHTML(
                pet.photo
            )}" alt="Pet">`;
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

    if (!container || !count) {
        return;
    }

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

    tasks.forEach(
        (task, index) => {

            if (task.done) {
                completed++;
            }

            const card =
                document.createElement(
                    "div"
                );

            card.className =
                "task-card glass" +
                (
                    task.done
                        ? " completed"
                        : ""
                );

            card.innerHTML = `

                <button
                    class="task-check ${
                        task.done
                            ? "done"
                            : ""
                    }"
                    onclick="toggleTask(${index})"
                >
                    ${
                        task.done
                            ? "✓"
                            : ""
                    }
                </button>

                <div class="task-info">

                    <strong>
                        ${escapeHTML(
                            task.title
                        )}
                    </strong>

                    <span>
                        ${escapeHTML(
                            task.time ||
                            "Today"
                        )}
                    </span>

                </div>
            `;

            container.appendChild(
                card
            );
        }
    );

    count.textContent =
        String(
            tasks.length -
            completed
        );
}


function toggleTask(index) {

    if (!tasks[index]) {
        return;
    }

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
            .getElementById(
                "reminderTitle"
            )
            .value
            .trim();

    const date =
        document
            .getElementById(
                "reminderDate"
            )
            .value;

    const time =
        document
            .getElementById(
                "reminderTime"
            )
            .value;

    const repeat =
        document
            .getElementById(
                "reminderRepeat"
            )
            .value;

    if (
        !title ||
        !date ||
        !time
    ) {

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

        alert(
            "Invalid date or time."
        );

        return;
    }

    if (
        when <= new Date()
    ) {

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

    reminders.push(
        reminder
    );

    saveReminders();

    scheduleNativeReminder(
        reminder
    );

    const reminderTitle =
        document.getElementById(
            "reminderTitle"
        );

    if (reminderTitle) {
        reminderTitle.value = "";
    }

    renderReminders();

    alert(
        "Reminder set successfully."
    );
}


function saveReminders() {

    localStorage.setItem(
        STORAGE.reminders,
        JSON.stringify(
            reminders
        )
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
                a.timestamp -
                b.timestamp
        )
        .forEach(
            reminder => {

                const card =
                    document.createElement(
                        "div"
                    );

                card.className =
                    "reminder-card glass";

                const repeatText =
                    reminder.repeat ===
                    "daily"
                        ? "Every day"
                        : reminder.repeat ===
                          "weekly"
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

                list.appendChild(
                    card
                );
            }
        );
}


function deleteReminder(id) {

    reminders =
        reminders.filter(
            reminder =>
                reminder.id !== id
        );

    saveReminders();

    renderReminders();

    cancelNativeReminder(
        id
    );
}


function formatReminderDate(
    timestamp
) {

    return new Date(
        timestamp
    ).toLocaleString(
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
        !window.AndroidPetCare
    ) {
        return;
    }

    if (
        typeof AndroidPetCare.scheduleReminder
        !== "function"
    ) {
        return;
    }

    try {

        AndroidPetCare.scheduleReminder(

            String(
                reminder.id
            ),

            String(
                reminder.title
            ),

            String(
                reminder.timestamp
            ),

            String(
                reminder.repeat || "once"
            )
        );

    } catch (error) {

        console.error(
            "Native reminder error:",
            error
        );
    }
}


function cancelNativeReminder(
    id
) {

    if (
        !window.AndroidPetCare
    ) {
        return;
    }

    if (
        typeof AndroidPetCare.cancelReminder
        !== "function"
    ) {
        return;
    }

    try {

        AndroidPetCare.cancelReminder(
            String(id)
        );

    } catch (error) {

        console.error(
            "Native cancel error:",
            error
        );
    }
}


/* =========================================================
   LOCATION / VET SEARCH
========================================================= */

/*
 * Check whether browser/WebView geolocation exists.
 */
function locationSupported() {

    return (
        "geolocation" in
        navigator
    );
}


/*
 * Show location loading state.
 */
function showVetLoading() {

    const results =
        document.getElementById(
            "vetResults"
        );

    if (!results) {
        return;
    }

    results.innerHTML = `
        <div class="empty-state glass">

            <p>
                Getting your location...
            </p>

            <p>
                Please allow location access
                when Android asks.
            </p>

        </div>
    `;
}


/*
 * Main nearby vet function.
 */
function findVets() {

    const results =
        document.getElementById(
            "vetResults"
        );

    if (!results) {
        return;
    }

    showVetLoading();

    if (!locationSupported()) {

        results.innerHTML = `
            <div class="empty-state glass">

                <p>
                    Location is not supported
                    on this device.
                </p>

            </div>
        `;

        return;
    }

    navigator.geolocation.getCurrentPosition(

        position => {

            const latitude =
                position.coords.latitude;

            const longitude =
                position.coords.longitude;

            const accuracy =
                Math.round(
                    position.coords.accuracy
                );

            showNearbyVetResults(
                latitude,
                longitude,
                accuracy
            );
        },

        error => {

            handleLocationError(
                error
            );
        },

        {
            enableHighAccuracy: true,

            timeout: 15000,

            maximumAge: 30000
        }
    );
}


/*
 * Display nearby veterinary search.
 */
function showNearbyVetResults(
    latitude,
    longitude,
    accuracy
) {

    const results =
        document.getElementById(
            "vetResults"
        );

    if (!results) {
        return;
    }

    /*
     * Google Maps search around
     * current GPS coordinates.
     */
    const mapsURL =
        "https://www.google.com/maps/search/" +
        encodeURIComponent(
            "veterinary hospital"
        ) +
        "/@" +
        latitude +
        "," +
        longitude +
        ",14z";

    /*
     * Avian/exotic veterinary search.
     */
    const avianURL =
        "https://www.google.com/maps/search/" +
        encodeURIComponent(
            "avian exotic veterinary hospital"
        ) +
        "/@" +
        latitude +
        "," +
        longitude +
        ",14z";

    results.innerHTML = `

        <div class="vet-card glass">

            <h3>
                Nearby veterinary hospitals
            </h3>

            <p>
                Location found successfully.
            </p>

            <p>
                GPS accuracy:
                approximately
                ${accuracy} metres
            </p>

            <div class="vet-actions">

                <button
                    class="vet-action"
                    onclick="openExternal(
                        '${mapsURL}'
                    )"
                >
                    Nearby Vets
                </button>

                <button
                    class="vet-action"
                    onclick="openExternal(
                        '${avianURL}'
                    )"
                >
                    Avian / Exotic Vets
                </button>

            </div>

        </div>
    `;
}


/*
 * Location error handling.
 */
function handleLocationError(
    error
) {

    const results =
        document.getElementById(
            "vetResults"
        );

    if (!results) {
        return;
    }

    let message =
        "Unable to get your location.";

    if (error) {

        if (
            error.code ===
            error.PERMISSION_DENIED
        ) {

            message =
                "Location permission was denied. " +
                "Please allow PetCare to access " +
                "your location from Android Settings.";

        } else if (
            error.code ===
            error.POSITION_UNAVAILABLE
        ) {

            message =
                "Location is currently unavailable. " +
                "Please turn ON GPS/location.";

        } else if (
            error.code ===
            error.TIMEOUT
        ) {

            message =
                "Location request timed out. " +
                "Please try again.";
        }
    }

    results.innerHTML = `

        <div class="empty-state glass">

            <p>
                ${escapeHTML(message)}
            </p>

            <button
                class="vet-action"
                onclick="findVets()"
            >
                Try Again
            </button>

        </div>
    `;
}


/* =========================================================
   EXTERNAL LINKS
========================================================= */

function openExternal(
    url
) {

    if (!url) {
        return;
    }

    /*
     * Try opening an external page.
     * This keeps the existing WebView app intact.
     */
    try {

        window.open(
            url,
            "_blank"
        );

    } catch (error) {

        window.location.href =
            url;
    }
}


/* =========================================================
   SECURITY / HTML ESCAPE
========================================================= */

function escapeHTML(
    value
) {

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