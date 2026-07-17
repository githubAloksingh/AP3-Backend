import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { SignupComponent } from './pages/signup/signup';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { ProfileComponent } from './pages/profile/profile';
import { AccountsListComponent } from './pages/accounts/accounts';
import { CreateAccountComponent } from './pages/accounts/create-account/create-account';
import { TransferComponent } from './pages/transfer/transfer';
import { TransactionsComponent } from './pages/transactions/transactions';
import { TransactionDetailsComponent } from './pages/transaction-details/transaction-details';
import { AdminComponent } from './pages/admin/admin';
import { ResetPasswordComponent } from './pages/reset-password/reset-password';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'accounts', component: AccountsListComponent, canActivate: [authGuard] },
  { path: 'accounts/create', component: CreateAccountComponent, canActivate: [authGuard] },
  { path: 'transfer', component: TransferComponent, canActivate: [authGuard] },
  { path: 'transactions', component: TransactionsComponent, canActivate: [authGuard] },
  { path: 'transactions/:id', component: TransactionDetailsComponent, canActivate: [authGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
