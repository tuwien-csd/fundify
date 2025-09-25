import { expect, test } from '@playwright/test';
import { E2E_CREDENTIALS } from '../config/E2ECredentialsStorage';

test.describe('Tests for institutions', () => {
  //Authenticate as institution
  test.use({ storageState: E2E_CREDENTIALS.INSTITUTION.FILE });
  test('Minimal test when authenticated as institution', async ({ page }) => {
    await page.goto('/home');
    await page.waitForURL('/home');
    // open sidebar

    await page.getByRole('link', { name: 'Institutions' }).click();
    await expect(page.getByRole('table')).toContainText('FWF');
    await page.getByRole('cell', { name: 'Austrian Science Fund' }).click();
    await page.getByText('Austrian Science Fund', { exact: true }).click();
  });
});
