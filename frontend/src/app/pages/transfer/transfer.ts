import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Account } from '../../models/account.model';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { TransactionService } from '../../services/transaction.service';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './transfer.html'
})
export class TransferComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);
  private readonly transactionService = inject(TransactionService);

  accounts = signal<Account[]>([]);
  loading = signal<boolean>(true);
  submitting = signal<boolean>(false);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');

  transferForm = this.fb.nonNullable.group({
    fromAccountNumber: ['', Validators.required],
    toAccountNumber: [''],
    receiverPhoneNumber: [''],
    amount: [1, [Validators.required, Validators.min(1)]],
    description: ['']
  });

  ngOnInit(): void {
    const customerId = this.authService.getCustomerId();
    console.log('[TransferComponent] customerId from localStorage:', customerId);

    this.errorMessage.set('');
    this.successMessage.set('');

    if (!customerId) {
      this.loading.set(false);
      this.errorMessage.set('Session not found. Please login again.');
      return;
    }

    this.accountService.getAccountsByCustomerId(customerId).subscribe({
      next: (accounts) => {
        console.log('[TransferComponent] accounts API returned:', accounts);
        const loadedAccounts = Array.isArray(accounts) ? accounts : [];
        this.accounts.set(loadedAccounts);

        if (loadedAccounts.length > 0) {
          const defaultAccount = loadedAccounts.find((account) => account.accountNumber)?.accountNumber;
          this.transferForm.patchValue({
            fromAccountNumber: defaultAccount ?? ''
          });
          this.errorMessage.set('');
        } else {
          this.transferForm.patchValue({ fromAccountNumber: '' });
          this.errorMessage.set(`No accounts found for this customer. Please create an account first.`);
        }

        this.loading.set(false);
      },
      error: (err) => {
        console.error('[TransferComponent] accounts API error:', err);
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load accounts.');
      }
    });
  }

  submit(): void {
    this.transferForm.markAllAsTouched();
    this.errorMessage.set('');
    this.successMessage.set('');

    if (this.transferForm.invalid) {
      return;
    }

    const value = this.transferForm.getRawValue();
    const receiverPhoneNumber = value.receiverPhoneNumber?.trim();
    const receiverAccountNumber = value.toAccountNumber?.trim();

    if (!receiverPhoneNumber && !receiverAccountNumber) {
      this.errorMessage.set('Please enter a receiver account number or receiver phone number.');
      return;
    }

    if (receiverPhoneNumber && receiverAccountNumber) {
      this.errorMessage.set('Please enter either a receiver phone number or an account number, not both.');
      return;
    }

    if (value.fromAccountNumber === receiverAccountNumber) {
      this.errorMessage.set('Sender and receiver accounts must be different.');
      return;
    }

    this.submitting.set(true);
    this.transactionService.transfer({
      fromAccountNumber: value.fromAccountNumber,
      toAccountNumber: receiverAccountNumber || undefined,
      receiverPhoneNumber: receiverPhoneNumber || undefined,
      amount: Number(value.amount),
      description: value.description?.trim() || undefined
    }).subscribe({
      next: (transaction) => {
        this.submitting.set(false);
        this.successMessage.set(`Transfer successful. Transaction #${transaction.id} created.`);
        this.transferForm.patchValue({ toAccountNumber: '', receiverPhoneNumber: '', amount: 1, description: '' });
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(err.error?.message || 'Transfer failed. Please verify account number, balance, and region.');
      }
    });
  }
}
