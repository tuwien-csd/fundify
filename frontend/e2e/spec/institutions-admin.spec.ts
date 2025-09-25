import { expect, test } from '@playwright/test';
import { E2E_CREDENTIALS } from '../config/E2ECredentialsStorage';
import { FUNDER_DETAILS_CONSTANTS } from '../../src/app/funders/funders.constants';
import { UNIVERSITY_DETAILS_CONSTANTS } from '../../src/app/universities/universities.constants';

test.describe('Tests for institution administration', () => {
  test.use({ storageState: E2E_CREDENTIALS.ADMIN.FILE });

  test('should successfully create, update and delete a funder', async ({
    page,
  }) => {
    await test.step('Create a new funder', async () => {
      await page.goto('/');
      await page.getByRole('link', { name: 'Institutions' }).click();
      await page.getByRole('button', { name: 'new' }).click();
      await page.getByRole('textbox', { name: 'Name' }).fill('Test Funder');
      await page
        .getByRole('combobox', { name: 'Language' })
        .locator('svg')
        .click();
      await page.getByRole('option', { name: 'De' }).click();
      await page.getByRole('textbox', { name: 'Street' }).fill('Test Street');
      await page.getByRole('textbox', { name: 'City' }).fill('Test City');
      await page
        .getByRole('textbox', { name: 'Postal Code' })
        .fill('Test Postal Code');
      await page
        .getByRole('textbox', { name: 'Country Code' })
        .fill('Test Country Code');
      await page.getByRole('textbox', { name: 'Website' }).fill('Test.at');
      await page.getByRole('button', { name: 'submit' }).click();
      const snackbar = page.locator('mat-snack-bar-container');
      await expect(snackbar).toContainText(
        FUNDER_DETAILS_CONSTANTS.CREATION_SUCCESS
      );
      await page.getByRole('button', { name: 'go back' }).click();
    });

    await test.step('Update the new funder', async () => {
      await page.getByRole('cell', { name: 'Test Funder' }).click();
      await page.getByRole('button', { name: 'edit' }).click();
      await page.getByRole('textbox', { name: 'Acronym' }).fill('Test ACR');
      await page.getByRole('button', { name: 'submit' }).click();
      await page.getByRole('button', { name: 'go back' }).click();
      const row = page.getByRole('row', {
        name: 'Test ACR Test Funder Test.at',
      });
      await expect(row).toBeVisible();
    });

    await test.step('delete new funder', async () => {
      const row = page.getByRole('row', {
        name: 'Test ACR Test Funder Test.at',
      });
      await row.getByRole('button').click();
      await page.getByRole('menuitem', { name: 'delete' }).click();
      await expect(row).not.toBeVisible();
    });
  });

  test('should successfully create, update and delete a university', async ({
    page,
  }) => {
    await test.step('Create a new university', async () => {
      await page.goto('/');
      await page.getByRole('link', { name: 'Institutions' }).click();
      await page.getByRole('tab', { name: 'Universities' }).click();
      await page.getByRole('button', { name: 'new' }).click();
      await page.getByRole('textbox', { name: 'Name' }).fill('Test University');
      await page.locator('path').click();
      await page.getByRole('option', { name: 'De' }).click();
      await page.getByRole('textbox', { name: 'Street' }).fill('Test Street');
      await page.getByRole('textbox', { name: 'City' }).fill('Test City');
      await page
        .getByRole('textbox', { name: 'Postal Code' })
        .fill('Test Postal Code');
      await page
        .getByRole('textbox', { name: 'Country Code' })
        .fill('Test Country Code');
      await page.getByRole('textbox', { name: 'Website' }).fill('Test.at');
      await page.getByRole('button', { name: 'submit' }).click();
      const snackbar = page.locator('mat-snack-bar-container');
      await expect(snackbar).toContainText(
        UNIVERSITY_DETAILS_CONSTANTS.CREATION_SUCCESS
      );
      await page.getByRole('button', { name: 'go back' }).click();
    });

    await test.step('Update the new university', async () => {
      await page.getByRole('cell', { name: 'Test University' }).click();
      await page.getByRole('button', { name: 'edit' }).click();
      await page.getByRole('textbox', { name: 'Acronym' }).fill('Test ACR');
      await page.getByRole('button', { name: 'submit' }).click();
      await page.getByRole('button', { name: 'go back' }).click();
      const row = page.getByRole('row', {
        name: 'Test ACR Test University Test.at',
      });
      await expect(row).toBeVisible();
    });

    await test.step('delete new university', async () => {
      const row = page.getByRole('row', {
        name: 'Test ACR Test University Test.at',
      });
      await row.getByRole('button').click();
      await page.getByRole('menuitem', { name: 'delete' }).click();
      await expect(row).not.toBeVisible();
    });
  });
});
