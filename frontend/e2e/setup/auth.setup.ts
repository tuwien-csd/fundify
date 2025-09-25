import { expect, Page, test as setup } from '@playwright/test';
import {
  E2E_CREDENTIALS,
  E2ECredential,
} from '../config/E2ECredentialsStorage';

setup('authenticate as admin', async ({ page }) => {
  await authenticateWithCredential(page, E2E_CREDENTIALS.ADMIN);
});

setup('authenticate as funder', async ({ page }) => {
  await authenticateWithCredential(page, E2E_CREDENTIALS.FUNDER);
});

setup('authenticate as institution', async ({ page }) => {
  await authenticateWithCredential(page, E2E_CREDENTIALS.INSTITUTION);
});

async function authenticateWithCredential(
  page: Page,
  credential: E2ECredential
) {
  await page.goto('/home');
  await page.getByRole('button', { name: 'login' }).click();
  await page
    .getByRole('textbox', { name: 'Username or email' })
    .fill(credential.USERNAME);
  await page
    .getByRole('textbox', { name: 'Password' })
    .fill(credential.PASSWORD);
  await page.getByRole('button', { name: 'Sign In' }).click();
  await expect(page.locator('app-user-widget')).toContainText(
    credential.USERNAME
  );
  await page.context().storageState({ path: credential.FILE });
}
