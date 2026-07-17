import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Transaction } from '../../models/transaction.model';
import { AuthService } from '../../services/auth.service';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './transactions.html'
})
export class TransactionsComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly transactionService = inject(TransactionService);

  transactions = signal<Transaction[]>([]);
  loading = signal<boolean>(true);
  errorMessage = signal<string>('');
  currentCustomerId = signal<number | null>(null);

  ngOnInit(): void {
    const customerId = this.authService.getCustomerId();
    if (!customerId) {
      this.loading.set(false);
      this.errorMessage.set('Session not found. Please login again.');
      return;
    }

    this.currentCustomerId.set(customerId);

    this.transactionService.getTransactions(customerId).subscribe({
      next: (transactions) => {
        this.transactions.set(transactions);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load transactions.');
      }
    });
  }
}
