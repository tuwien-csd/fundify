import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { BackendService } from './backend.service';
import { StandardizedSubject } from '../../shared/models/interfaces/standardizedSubject.interface';

@Injectable({
  providedIn: 'root',
})
export class SubjectService {
  private backendService = inject(BackendService);

  private entityPathPart = 'oefos';
  private oefosList: StandardizedSubject[] = [];

  fetchSubjectsFromBackend(): Observable<StandardizedSubject[]> {
    return this.backendService
      .get<StandardizedSubject[]>(this.entityPathPart)
      .pipe(
        map((oefos) =>
          [...oefos].sort((a, b) => this.compareCodes(a.code, b.code))
        ),
        tap((sortedOefos) => {
          this.oefosList = sortedOefos;
        })
      );
  }

  private compareCodes(codeA: string, codeB: string): number {
    const numA = parseInt(codeA.padEnd(3, '0'));
    const numB = parseInt(codeB.padEnd(3, '0'));
    return numA - numB;
  }

  getSubjects(): StandardizedSubject[] {
    return this.oefosList;
  }
}
