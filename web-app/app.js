// App State
let currentUser = null;
let currentNoteId = null;
let currentNoteColor = null;
let notes = [];
let previousHash = window.location.hash || '#/';
let isSaving = false;

// DOM Elements
const mainScreen = document.getElementById('main-screen');
const noteScreen = document.getElementById('note-screen');
const accountScreen = document.getElementById('account-screen');
const loginDetail = document.getElementById('login-detail');
const loginInfo = document.getElementById('login-info');
const addNoteInfo = document.getElementById('add-note-info');
const notesSection = document.getElementById('notes-section');
const notesContainer = document.getElementById('notes-container');
const addNoteBtn = document.getElementById('add-note-btn');
const noteTitle = document.getElementById('note-title');
const noteContent = document.getElementById('note-content');
const titleRule = document.getElementById('title-rule');
const noteHeader = document.getElementById('note-header');
const colorBtn = document.getElementById('color-btn');
const deleteNoteBtn = document.getElementById('delete-note-btn');
const backBtn = document.getElementById('back-btn');
const accountBackBtn = document.getElementById('account-back-btn');
const colorPickerModal = document.getElementById('color-picker-modal');
const confirmModal = document.getElementById('confirm-modal');
const confirmTitle = document.getElementById('confirm-title');
const confirmBtn = document.getElementById('confirm-btn');
const cancelBtn = document.getElementById('cancel-btn');
const usernameInput = document.getElementById('username-input');
const usernameRule = document.getElementById('username-rule');
const saveUsernameBtn = document.getElementById('save-username-btn');
const logoutBtn = document.getElementById('logout-btn');
const saveNoteBtn = document.getElementById('save-note-btn');
const toast = document.getElementById('toast');

// Utility Functions
function showScreen(screen) {
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    screen.classList.add('active');

    // Update body class for styling if needed
    document.body.className = screen.id;
}

// Router
function router() {
    const hash = window.location.hash || '#/';

    // Navigation Guard for New Note
    if (previousHash === '#/note/new' && hash !== '#/note/new' && !isSaving) {
        const title = noteTitle.value.trim();
        const content = noteContent.value.trim();
        if (title || content) {
            alert("The note is not yet saved");
            // Revert hash without triggering guard again
            isSaving = true;
            window.location.hash = '#/note/new';
            return;
        }
    }

    isSaving = false;
    previousHash = hash;

    if (hash === '#/' || hash === '#') {
        showScreen(mainScreen);
    } else if (hash === '#/account') {
        if (currentUser) {
            usernameInput.value = loginDetail.textContent;
            showScreen(accountScreen);
        } else {
            window.location.hash = '#/';
        }
    } else if (hash === '#/note/new') {
        if (currentUser) {
            // Only initialize UI if we aren't already on the new note screen
            if (!noteScreen.classList.contains('active') || currentNoteId !== null) {
                createNewNoteUI();
            }
        } else {
            window.location.hash = '#/';
        }
    } else if (hash.startsWith('#/note/')) {
        const noteId = hash.replace('#/note/', '');
        const note = notes.find(n => n.noteKey === noteId);
        if (note) {
            // Only initialize UI if we aren't already on this specific note
            if (currentNoteId !== noteId) {
                openNoteUI(note);
            }
        } else if (notes.length > 0) {
            // Note might still be loading, or doesn't exist
            // For now, if we have notes and it's not found, go back
            window.location.hash = '#/';
        }
    }
}

window.addEventListener('hashchange', router);
window.addEventListener('load', router);

function showToast(message) {
    toast.textContent = message;
    toast.classList.remove('hidden');
    setTimeout(() => {
        toast.classList.add('hidden');
    }, 3000);
}

function getRandomColor() {
    const colors = ['#FF9E9E', '#91F48F', '#FFF599', '#9EFFFF', '#B69CFF'];
    return colors[Math.floor(Math.random() * colors.length)];
}

function changeBgTint(element, color) {
    element.style.backgroundColor = color;
}

// Auth State Listener
auth.onAuthStateChanged((user) => {
    currentUser = user;
    if (user) {
        loadUserData();
        updateUI();
    } else {
        loginDetail.textContent = 'login';
        window.location.hash = '#/';
        updateUI();
    }
    router(); // Re-run router on auth change
});

// Load User Data
function loadUserData() {
    if (!currentUser) return;

    const userId = currentUser.uid;
    const usernameRef = database.ref(`${userId}/username`);
    const notesRef = database.ref(`${userId}/allNotes`);

    // Load username
    usernameRef.on('value', (snapshot) => {
        const username = snapshot.val();
        if (username) {
            loginDetail.textContent = username;
        } else {
            // Create username from email
            const email = currentUser.email;
            const name = email ? email.substring(0, email.indexOf('@')) : 'user';
            const shortName = name.length > 11 ? name.substring(0, 11) : name;
            usernameRef.set(shortName);
            loginDetail.textContent = shortName;
        }
    });

    // Load notes
    notesRef.on('value', (snapshot) => {
        notes = [];
        const data = snapshot.val();
        if (data) {
            Object.keys(data).forEach(key => {
                notes.push({ ...data[key], noteKey: key });
            });
            notes.reverse();
        }
        renderNotes();
        updateUI();
        router(); // Re-run router in case we were waiting for a note to load
    });
}

// Update UI based on auth state
function updateUI() {
    if (!currentUser) {
        loginInfo.classList.remove('hidden');
        addNoteInfo.classList.add('hidden');
        notesSection.classList.add('hidden');
        addNoteBtn.classList.add('hidden');
    } else {
        loginInfo.classList.add('hidden');
        if (notes.length === 0) {
            addNoteInfo.classList.remove('hidden');
            notesSection.classList.add('hidden');
        } else {
            addNoteInfo.classList.add('hidden');
            notesSection.classList.remove('hidden');
        }
        addNoteBtn.classList.remove('hidden');
    }
}

// Render Notes
function renderNotes() {
    notesContainer.innerHTML = '';
    notes.forEach(note => {
        const noteCard = document.createElement('div');
        noteCard.className = 'note-card';
        const color = note.noteColor || getRandomColor();
        noteCard.style.backgroundColor = color;

        const noteText = note.noteTitle ||
            (note.note.length > 90 ? note.note.substring(0, 97) + '...' : note.note);

        noteCard.textContent = noteText;
        noteCard.addEventListener('click', () => {
            window.location.hash = `#/note/${note.noteKey}`;
        });
        notesContainer.appendChild(noteCard);
    });
}

// UI helpers that don't change hash
function openNoteUI(note) {
    currentNoteId = note.noteKey;
    currentNoteColor = note.noteColor || getRandomColor();

    noteTitle.value = note.noteTitle || '';
    noteContent.value = note.note || '';

    // Update header color
    changeBgTint(noteHeader, currentNoteColor);
    changeBgTint(colorBtn, currentNoteColor);

    // Hide save button for existing notes
    saveNoteBtn.classList.add('hidden');

    showScreen(noteScreen);

    // If note doesn't have color, save it
    if (!note.noteColor && currentUser) {
        const userId = currentUser.uid;
        database.ref(`${userId}/allNotes/${currentNoteId}/noteColor`).set(currentNoteColor);
    }
}

function createNewNoteUI() {
    currentNoteId = null;
    currentNoteColor = getRandomColor();

    noteTitle.value = '';
    noteContent.value = '';

    changeBgTint(noteHeader, currentNoteColor);
    changeBgTint(colorBtn, currentNoteColor);

    // Show save button for new notes
    saveNoteBtn.classList.remove('hidden');

    showScreen(noteScreen);
}

// Open Note
function openNote(note) {
    window.location.hash = `#/note/${note.noteKey}`;
}

// Create New Note
function createNewNote() {
    window.location.hash = '#/note/new';
}

// Save Note
function saveNote() {
    if (!currentUser) return;

    const title = noteTitle.value.trim();
    const content = noteContent.value.trim();

    if (!title && !content) return;

    const userId = currentUser.uid;
    const noteData = {
        noteTitle: title,
        note: content,
        noteColor: currentNoteColor
    };

    if (currentNoteId) {
        // Update existing note
        noteData.noteKey = currentNoteId;
        database.ref(`${userId}/allNotes/${currentNoteId}`).set(noteData);
    } else {
        // Create new note
        const newNoteRef = database.ref(`${userId}/allNotes`).push();
        currentNoteId = newNoteRef.key;
        noteData.noteKey = currentNoteId;
        newNoteRef.set(noteData);
    }
}

// Delete Note
function deleteNote() {
    if (!currentUser || !currentNoteId) {
        window.location.hash = '#/';
        return;
    }

    const userId = currentUser.uid;
    database.ref(`${userId}/allNotes/${currentNoteId}`).remove();
    window.location.hash = '#/';
}

// Show Color Picker
function showColorPicker() {
    colorPickerModal.classList.remove('hidden');
}

// Hide Color Picker
function hideColorPicker() {
    colorPickerModal.classList.add('hidden');
}

// Change Note Color
function changeNoteColor(color) {
    currentNoteColor = color;
    changeBgTint(noteHeader, color);
    changeBgTint(colorBtn, color);
    saveNote();
    hideColorPicker();
}

// Show Confirmation Dialog
function showConfirmDialog(title, confirmText, onConfirm) {
    confirmTitle.textContent = title;
    confirmBtn.textContent = confirmText;
    confirmModal.classList.remove('hidden');

    confirmBtn.onclick = () => {
        onConfirm();
        confirmModal.classList.add('hidden');
    };
}

// Google Sign In
function signInWithGoogle() {
    const provider = new firebase.auth.GoogleAuthProvider();
    provider.addScope('email');
    provider.addScope('profile');

    auth.signInWithPopup(provider)
        .then((result) => {
            // User signed in
            console.log('Sign in successful:', result.user);
            showToast('Successfully signed in!');
        })
        .catch((error) => {
            console.error('Sign in error:', error);
            console.error('Error code:', error.code);
            console.error('Error message:', error.message);

            // Try redirect method if popup is blocked
            if (error.code === 'auth/popup-blocked' || error.code === 'auth/popup-closed-by-user') {
                auth.signInWithRedirect(provider);
            } else {
                let errorMsg = 'Sign in failed. ';
                if (error.code === 'auth/unauthorized-domain') {
                    const currentDomain = window.location.hostname || 'localhost';
                    errorMsg += `Domain "${currentDomain}" not authorized. Go to Firebase Console > Authentication > Settings > Authorized domains and add "${currentDomain}"`;
                } else if (error.code === 'auth/operation-not-allowed') {
                    errorMsg += 'Google sign-in not enabled. Go to Firebase Console > Authentication > Sign-in method and enable Google.';
                } else {
                    errorMsg += error.message || 'Please try again.';
                }
                showToast(errorMsg);
                console.error('Full error details:', error);
            }
        });
}

// Logout
function logout() {
    auth.signOut()
        .then(() => {
            showToast('Successfully logged out');
            window.location.hash = '#/';
        })
        .catch((error) => {
            console.error('Logout error:', error);
            showToast('Logout failed. Please try again.');
        });
}

// Save Username
function saveUsername() {
    if (!currentUser) return;

    const username = usernameInput.value.trim();

    if (!username || username === 'login' || username === 'लॉगिन' || username === 'લોગિન') {
        showToast('Invalid Username');
        return;
    }

    const userId = currentUser.uid;
    database.ref(`${userId}/username`).set(username)
        .then(() => {
            loginDetail.textContent = username;
            showToast('Successfully changed username');
            window.location.hash = '#/';
        })
        .catch((error) => {
            console.error('Error saving username:', error);
            showToast('Failed to save username');
        });
}

// Event Listeners

// Login
loginDetail.addEventListener('click', () => {
    if (!currentUser) {
        signInWithGoogle();
    } else {
        window.location.hash = '#/account';
    }
});

// Add Note Button
addNoteBtn.addEventListener('click', createNewNote);

// Back Buttons (Now handled by hash/browser back)
// These listeners will be removed once buttons are deleted from HTML

// Note Editor - Auto Save for existing notes
let saveTimeout;
function debounceSave() {
    if (!currentNoteId) return; // Only auto-save for existing notes
    clearTimeout(saveTimeout);
    saveTimeout = setTimeout(saveNote, 500);
}

// Note Editor - Event Listeners
noteTitle.addEventListener('input', () => {
    debounceSave();
    if (noteTitle.value.length === 100) {
        titleRule.classList.remove('hidden');
    } else {
        titleRule.classList.add('hidden');
    }
});

noteContent.addEventListener('input', debounceSave);

saveNoteBtn.addEventListener('click', () => {
    isSaving = true;
    saveNote();
    window.location.hash = '#/';
});

// Browser-level navigation guard
window.addEventListener('beforeunload', (e) => {
    if (window.location.hash === '#/note/new' && !isSaving) {
        const title = noteTitle.value.trim();
        const content = noteContent.value.trim();
        if (title || content) {
            e.preventDefault();
            e.returnValue = '';
        }
    }
});

// Color Picker
colorBtn.addEventListener('click', showColorPicker);

document.querySelectorAll('.color-option').forEach(btn => {
    btn.addEventListener('click', () => {
        changeNoteColor(btn.dataset.color);
    });
});

// Delete Note
deleteNoteBtn.addEventListener('click', () => {
    if (currentNoteId) {
        showConfirmDialog('Confirm Delete', 'Delete', deleteNote);
    } else {
        window.location.hash = '#/';
    }
});

// Account Management
saveUsernameBtn.addEventListener('click', saveUsername);

logoutBtn.addEventListener('click', () => {
    showConfirmDialog('Confirm Log Out', 'Log Out', logout);
});

// Username Input
usernameInput.addEventListener('input', () => {
    if (usernameInput.value.length === 12) {
        usernameRule.classList.remove('hidden');
    } else {
        usernameRule.classList.add('hidden');
    }
});

// Modal Close
cancelBtn.addEventListener('click', () => {
    confirmModal.classList.add('hidden');
    colorPickerModal.classList.add('hidden');
});

// Close modals on background click
colorPickerModal.addEventListener('click', (e) => {
    if (e.target === colorPickerModal) {
        hideColorPicker();
    }
});

confirmModal.addEventListener('click', (e) => {
    if (e.target === confirmModal) {
        confirmModal.classList.add('hidden');
    }
});

// Check for redirect result (if user was redirected for sign-in)
auth.getRedirectResult()
    .then((result) => {
        if (result.user) {
            console.log('Sign in successful via redirect:', result.user);
            showToast('Successfully signed in!');
        }
    })
    .catch((error) => {
        console.error('Redirect sign in error:', error);
    });

// Initialize
updateUI();

