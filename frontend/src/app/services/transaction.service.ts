import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Transaction, TransferRequest } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/transactions`;

  transfer(request: TransferRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/transfer`, request);
  }

  getTransactions(customerId?: number): Observable<Transaction[]> {
    let params = new HttpParams();
    if (customerId) {
      params = params.set('customerId', String(customerId));
    }
    return this.http.get<Transaction[]>(this.apiUrl, { params });
  }

  getTransactionById(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/${id}`);
  }
}
