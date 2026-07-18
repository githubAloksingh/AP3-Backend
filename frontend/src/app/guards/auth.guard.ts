import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    if (authService.isAdmin() && (state.url.startsWith('/dashboard') || state.url.startsWith('/accounts'))) {
      router.navigate(['/admin']);
      return false;
    }
    return true;
  }

  // Not logged in so redirect to login page
  router.navigate(['/login']);
  return false;
};
