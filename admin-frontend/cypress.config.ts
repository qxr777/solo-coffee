import { defineConfig } from 'cypress'

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:5173',
    specPattern: 'tests/e2e/specs/**/*.cy.ts',
    supportFile: 'tests/e2e/support/commands.ts',
    fixturesFolder: 'tests/fixtures',
    video: false,
    screenshotOnRunFailure: false
  },
  cacheFolder: './cypress-cache'
})
