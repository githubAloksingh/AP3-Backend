import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Transaction } from '../../models/transaction.model';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-transaction-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './transaction-details.html'
})
export class TransactionDetailsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly transactionService = inject(TransactionService);

  transaction = signal<Transaction | null>(null);
  loading = signal<boolean>(true);
  errorMessage = signal<string>('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.loading.set(false);
      this.errorMessage.set('Invalid transaction id.');
      return;
    }

    this.transactionService.getTransactionById(id).subscribe({
      next: (transaction) => {
        this.transaction.set(transaction);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load transaction.');
      }
    });
  }
}
