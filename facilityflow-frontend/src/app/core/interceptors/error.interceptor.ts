import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';

/**
 * Surfaces API errors as toasts so feature components don't need to
 * duplicate error-handling boilerplate on every subscribe().
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toast = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401) {
        const message = error.error?.message ?? 'Something went wrong. Please try again.';
        toast.error(message);
      }
      return throwError(() => error);
    }),
  );
};
