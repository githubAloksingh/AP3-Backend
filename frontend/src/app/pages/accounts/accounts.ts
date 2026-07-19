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
  showBalances = signal<boolean>(false);

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

  closeAccount(accountId: number | undefined): void {
    if (!accountId) return;
    if (confirm('Are you sure you want to close this account? All remaining funds will be deleted, and this action cannot be undone.')) {
      this.accountService.deleteAccount(accountId).subscribe({
        next: () => {
          console.log('[AccountsListComponent] Account closed successfully:', accountId);
          const customerId = this.authService.getCustomerId();
          if (customerId) {
            this.loadAccounts(customerId);
          }
        },
        error: (err) => {
          console.error('[AccountsListComponent] Account close error:', err);
          this.errorMessage.set(err.error?.message || 'Failed to close account.');
        }
      });
    }
  }

  protected maskAccountNumber(accountNumber?: string): string {
    if (!accountNumber) return '••••';
    const visibleDigits = 4;
    return `${'•'.repeat(Math.max(0, accountNumber.length - visibleDigits))}${accountNumber.slice(-visibleDigits)}`;
  }

  protected toggleBalances(): void {
    this.showBalances.update(value => !value);
  }
}
