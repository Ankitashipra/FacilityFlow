import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/enums';

/**
 * Usage: { path: 'users', canActivate: [roleGuard(['ADMIN'])], ... }
 */
export const roleGuard = (allowedRoles: Role[]): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.role() && allowedRoles.includes(auth.role()!)) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
