import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  //TODO: Add type safety to the keys and values stored in local storage.
  save(key: LocalStorageKey, data: unknown): void {
    if (typeof data === 'object') {
      localStorage.setItem(key, JSON.stringify(data));
    } else {
      localStorage.setItem(key, String(data));
    }
  }

  load(key: LocalStorageKey): unknown {
    const data = localStorage.getItem(key);
    if (data === null) {
      return null;
    }
    try {
      return JSON.parse(data);
    } catch (error: unknown) {
      console.error(
        'Error parsing data from localStorage for key:',
        key,
        error
      );
      console.error('Returning contents as string instead.');
      return data;
    }
  }

  remove(key: string): void {
    localStorage.removeItem(key);
  }
}

export const LOCAL_STORAGE_KEYS = {
  STAGED_FUNDER_CHANGES: 'stagedFunderChanges',
  STAGED_CALL_CHANGES: 'stagedCallChanges',
  STAGED_PROGRAM_CHANGES: 'stagedProgramChanges',
  USER: 'user',
  ACCESS_TOKEN: 'access_token',
} as const;

type LocalStorageKey =
  (typeof LOCAL_STORAGE_KEYS)[keyof typeof LOCAL_STORAGE_KEYS];
