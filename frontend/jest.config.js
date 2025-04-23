export default {
    testEnvironment: 'jsdom',
    transform: {
        '^.+\\.vue$': '@vue/vue3-jest',
        '^.+\\.[jt]sx?$': 'babel-jest'
    },
    moduleFileExtensions: ['vue', 'js', 'json'],
    moduleNameMapper: {
        '^@vue/test-utils': '<rootDir>/node_modules/@vue/test-utils/dist/vue-test-utils.cjs.js',
        '^@/(.*)$': '<rootDir>/src/$1'
    },
    setupFilesAfterEnv: ['<rootDir>/setup-jest.js'],
    testMatch: ['**/__tests__/**/*.spec.js'],
    transformIgnorePatterns: [
        '/node_modules/(?!(@testing-library|@vue|vue))'
    ],
}