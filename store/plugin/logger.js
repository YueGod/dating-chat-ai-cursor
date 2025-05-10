// Simple mock implementation of the Vuex logger plugin
export default function createLogger(options = {}) {
    return store => {
        // Subscribe to store mutations
        store.subscribe((mutation, state) => {
            // Simple console logging
            if (process.env.NODE_ENV !== 'production') {
                console.group(`%c MUTATION: ${mutation.type}`, 'color: #03A9F4; font-weight: bold')
                console.log('%c prev state', 'color: #9E9E9E; font-weight: bold', options.transformer ? options.transformer(state) : state)
                console.log('%c mutation', 'color: #4CAF50; font-weight: bold', mutation.payload)
                console.log('%c next state', 'color: #FF5722; font-weight: bold', options.transformer ? options.transformer(state) : state)
                console.groupEnd()
            }
        })
    }
} 