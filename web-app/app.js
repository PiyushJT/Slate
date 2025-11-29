// App State
let currentUser = null;
let currentNoteId = null;
let currentNoteColor = null;
let notes = [];

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
const toast = document.getElementById('toast');

// Utility Functions
function showScreen(screen) {
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    screen.classList.add('active');
}

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
        showScreen(mainScreen);
        updateUI();
    }
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
        noteCard.addEventListener('click', () => openNote(note));
        notesContainer.appendChild(noteCard);
    });
}

// Open Note
function openNote(note) {
    currentNoteId = note.noteKey;
    currentNoteColor = note.noteColor || getRandomColor();
    
    noteTitle.value = note.noteTitle || '';
    noteContent.value = note.note || '';
    
    // Update header color
    changeBgTint(noteHeader, currentNoteColor);
    changeBgTint(colorBtn, currentNoteColor);
    
    showScreen(noteScreen);
    
    // If note doesn't have color, save it
    if (!note.noteColor && currentUser) {
        const userId = currentUser.uid;
        database.ref(`${userId}/allNotes/${currentNoteId}/noteColor`).set(currentNoteColor);
    }
}

// Create New Note
function createNewNote() {
    currentNoteId = null;
    currentNoteColor = getRandomColor();
    
    noteTitle.value = '';
    noteContent.value = '';
    
    changeBgTint(noteHeader, currentNoteColor);
    changeBgTint(colorBtn, currentNoteColor);
    
    showScreen(noteScreen);
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
        showScreen(mainScreen);
        return;
    }
    
    const userId = currentUser.uid;
    database.ref(`${userId}/allNotes/${currentNoteId}`).remove();
    showScreen(mainScreen);
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
            showScreen(mainScreen);
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
            showScreen(mainScreen);
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
        usernameInput.value = loginDetail.textContent;
        showScreen(accountScreen);
    }
});

// Add Note Button
addNoteBtn.addEventListener('click', createNewNote);

// Back Buttons
backBtn.addEventListener('click', () => {
    showScreen(mainScreen);
});

accountBackBtn.addEventListener('click', () => {
    showScreen(mainScreen);
});

// Note Editor - Auto Save
let saveTimeout;
function debounceSave() {
    clearTimeout(saveTimeout);
    saveTimeout = setTimeout(saveNote, 500);
}

noteTitle.addEventListener('input', () => {
    debounceSave();
    if (noteTitle.value.length === 100) {
        titleRule.classList.remove('hidden');
    } else {
        titleRule.classList.add('hidden');
    }
});

noteContent.addEventListener('input', debounceSave);

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
        showScreen(mainScreen);
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

