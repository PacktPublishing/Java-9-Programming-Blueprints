var bus = new Vue();

var NotesActions = {
    buildAuthHeader: function () {
        return new Headers({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + NotesActions.getCookie('Bearer')
        });
    },
    fetchNotes: function () {
        fetch('api/notes', {
            headers: this.buildAuthHeader()
        })
        .then(function (response) {
            store.state.loggedIn = response.status === 200;
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (notes) {
            store.commit('setNotes', notes);
        });
    },
    deleteNote: function (index) {
        var note = store.state.notes[index];
        fetch('api/notes/' + note.id, {
            method: 'delete',
            headers: this.buildAuthHeader()
        })
        .then(function (response) {
            if (response.status !== 200) {
                console.log('Looks like there was a problem. Status Code: ' +
                        response.status);
                return;
            }
            store.commit('deleteNote', index);
        });
    },
    saveNote: function (index, note) {
        var uri = 'api/notes/';
        var method = 'post';
        if (note.id) {
            uri += note.id;
            method = 'put';
        }
        fetch(uri, {
            method: method,
            headers: this.buildAuthHeader(),
            body: JSON.stringify(note)
        })
        .then(function (response) {
            if (response.status !== 201 && response.status !== 200) {
                console.log('Looks like there was a problem. Status Code: ' +
                        response.status);
                return;
            }
            if (index > -1) {
                store.commit('updateNote', note);
            } else {
                store.commit('addNote', note);
            }
        })
        .catch(function (error) {
            console.log('Request failed', error);
        });
    },
    getCookie: function (name) {
        var value = null;
        document.cookie.split(/\s*;\s*/).forEach(function (cookie) {
            var parts = cookie.split("=");
            if (parts[0] === name) {
                value = parts[1];
            }
        });
        return value;
    },
    newNote: function () {
        return {
            id: null,
            title: '',
            body: '',
            created: new Date()
        }
    }
};

const store = new Vuex.Store({
    state: {
        notes: [],
        loggedIn: false,
        currentIndex: -1,
        currentNote: NotesActions.newNote()
    },
    mutations: {
        setNotes(state, notes) {
            state.notes = [];
            if (notes) {
                notes.forEach(i => {
                    state.notes.push({
                        id: i.id,
                        title: i.title,
                        body: i.body,
                        created: new Date(i.created),
                        modified: new Date(i.modified)
                    });
                });
            }
        },
        addNote(state, note) {
            state.notes.push({
                id: note.id,
                title: note.title,
                body: note.body,
                created: typeof note.created === 'string' ?
                        new Date(note.created) : note.created,
                modified: typeof note.modified === 'string' ?
                        new Date(note.modified) : note.modified
            });
        },
        updateNote(state, note) {
            state.notes.splice(state.currentIndex, 1, {
                id: note.id,
                title: note.title,
                body: note.body,
                created: new Date(note.created),
                modified: new Date(note.modified)
            });
        },
        deleteNote(state, index) {
            state.notes.splice(index, 1);
        },
        noteClicked(state, index) {
            state.currentIndex = index;
            state.currentNote = state.notes[index];
            bus.$emit('note-clicked', state.currentNote);
        }
    }
});

Vue.component('navbar', {
    template: '#navbar-template',
    store,
    data: function () {
        return {
            authUrl: "#"
        };
    },
    computed: {
        isLoggedIn() {
            return this.$store.state.loggedIn;
        }
    },
    methods: {
        add: function () {
            bus.$emit('add-clicked');
        },
        logout: function () {
            document.cookie = "Bearer=''; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT";
            location.reload();
        },
        getAuthUrl: function () {
            var self = this;
            fetch('api/auth/url')
                .then(function (response) {
                    return response.text();
                })
                .then(function (url) {
                    self.authUrl = url;
                });
        }
    },
    mounted: function () {
        this.getAuthUrl();
    }
});

Vue.component('note-list', {
    template: '#note-list-template',
    store,
    computed: {
        notes() {
            return this.$store.state.notes;
        },
        isLoggedIn() {
            return this.$store.state.loggedIn;
        }
    },
    methods: {
        loadNote: function (index) {
            this.$store.commit('noteClicked', index);
        },
        deleteNote: function (index) {
            if (confirm("Are you sure want to delete this note?")) {
                NotesActions.deleteNote(index);
            }
        }
    }
});

Vue.component('note-form', {
    template: '#note-form-template',
    store,
    data: function () {
        return {
            note: NotesActions.newNote()
        };
    },
    computed: {
        index() {
            return this.$store.state.currentIndex;
        },
        isLoggedIn() {
            return this.$store.state.loggedIn;
        }
    },
    methods: {
        save: function () {
            NotesActions.saveNote(this.index, {
                id: this.note.id,
                title: this.note.title,
                body: CKEDITOR.instances.notebody.getData(),
                created: this.note.created
            });
            this.clearForm();
        },
        clearForm: function () {
            this.$data.note = NotesActions.newNote();
            CKEDITOR.instances.notebody.setData('');
        },
        updateForm: function (note) {
            this.$data.note = note;
            if (CKEDITOR.instances.notebody) {
                CKEDITOR.instances.notebody.setData(note.body);
            }
        }
    },
    mounted: function () {
        var self = this;
        bus.$on('add-clicked', function () {
            self.$store.currentNote = NotesActions.newNote();
            self.clearForm();
        });
        bus.$on('note-clicked', function (note) {
            self.updateForm(note);
        });
        CKEDITOR.replace('notebody');
    }
});

var vm = new Vue({
    el: '#app',
    store,
    computed: {
        isLoggedIn() {
            return this.$store.state.loggedIn;
        },
        notes() {
            return this.$store.state.notes;
        }
    },
    created: function () {
        NotesActions.fetchNotes();
    }
});