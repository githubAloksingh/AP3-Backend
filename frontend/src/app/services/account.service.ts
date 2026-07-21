import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Account } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/accounts`;

  getAccountsByCustomerId(customerId: number): Observable<Account[]> {
    const params = new HttpParams().set('customerId', String(customerId));
    return this.http.get<Account[]>(this.apiUrl, { params });
  }

  getAllAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  createAccount(account: Account): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account);
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  requestAccountDeletion(id: number): Observable<Account> {
    return this.http.post<Account>(`${this.apiUrl}/${id}/request-deletion`, {});
  }

  approveAccountDeletion(id: number): Observable<Account> {
    return this.http.post<Account>(`${this.apiUrl}/${id}/approve-deletion`, {});
  }

  rejectAccountDeletion(id: number): Observable<Account> {
    return this.http.post<Account>(`${this.apiUrl}/${id}/reject-deletion`, {});
  }

  switchAccountType(id: number): Observable<Account> {
    return this.http.put<Account>(`${this.apiUrl}/${id}/switch-type`, {});
  }
}
