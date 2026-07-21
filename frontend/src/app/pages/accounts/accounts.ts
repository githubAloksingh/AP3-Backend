import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { Account } from '../../models/account.model';

@Component({
  selector: 'app-accounts-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './accounts.html'
})
export class AccountsListComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);

  accounts = signal<Account[]>([]);
  loading = signal<boolean>(true);
  errorMessage = signal<string>('');
  visibleAccountId = signal<number | null>(null);

  ngOnInit(): void {
    const customerId = this.authService.getCustomerId();
    if (customerId) {
      this.loadAccounts(customerId);
    } else {
      this.loading.set(false);
      this.errorMessage.set('Session not found.');
    }
  }

  loadAccounts(customerId: number): void {
    console.log('[AccountsListComponent] Loading accounts for customerId:', customerId);
    this.accountService.getAccountsByCustomerId(customerId).subscribe({
      next: (data) => {
        console.log('[AccountsListComponent] Accounts loaded successfully:', data);
        this.accounts.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('[AccountsListComponent] Accounts load error:', err);
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load accounts.');
      }
    });
  }

  requestDeletion(accountId: number | undefined): void {
    if (!accountId) return;
    if (confirm('Are you sure you want to request deletion of this account? Your request will be sent to the admin for approval.')) {
      this.accountService.requestAccountDeletion(accountId).subscribe({
        next: () => {
          const customerId = this.authService.getCustomerId();
          if (customerId) {
            this.loadAccounts(customerId);
          }
        },
        error: (err) => {
          this.errorMessage.set(err.error?.message || 'Failed to submit deletion request.');
        }
      });
    }
  }

  switchAccountType(account: Account): void {
    if (!account.id) return;
    const targetType = account.accountType === 'SAVINGS' ? 'CURRENT' : 'SAVINGS';
    if (confirm(`Do you want to convert this account to a ${targetType} account?`)) {
      this.accountService.switchAccountType(account.id).subscribe({
        next: () => {
          const customerId = this.authService.getCustomerId();
          if (customerId) {
            this.loadAccounts(customerId);
          }
        },
        error: (err) => {
          this.errorMessage.set(err.error?.message || 'Failed to switch account type.');
        }
      });
    }
  }

  protected maskAccountNumber(accountNumber?: string): string {
    if (!accountNumber) return '••••';
    const visibleDigits = 4;
    return `${'•'.repeat(Math.max(0, accountNumber.length - visibleDigits))}${accountNumber.slice(-visibleDigits)}`;
  }

  protected toggleBalance(accountId: number | undefined): void {

    if (!accountId) return;

    this.visibleAccountId.update(current =>
      current === accountId ? null : accountId
    );

  }

}
