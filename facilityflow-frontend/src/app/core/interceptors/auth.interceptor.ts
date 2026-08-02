import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenStorageService } from '../services/token-storage.service';
import { AuthService } from '../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../models/api-response.model';
import { AuthResponse } from '../models/user.model';

/**
 * Attaches the bearer access token to every outgoing request and, on a 401
 * from an authenticated request, attempts a single silent refresh before
 * giving up and forcing logout.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const http = inject(HttpClient);
  const auth = inject(AuthService);

  const accessToken = tokenStorage.getAccessToken();
  const authReq = accessToken ? req.clone({ setHeaders: { Authorization: `Bearer ${accessToken}` } }) : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthEndpoint = req.url.includes('/auth/login') || req.url.includes('/auth/register') || req.url.includes('/auth/refresh');

      if (error.status === 401 && !isAuthEndpoint && tokenStorage.getRefreshToken()) {
        const refreshToken = tokenStorage.getRefreshToken();
        return http.post<ApiResponse<AuthResponse>>(`${environment.apiUrl}/auth/refresh`, { refreshToken }).pipe(
          switchMap(res => {
            tokenStorage.setSession(res.data.accessToken, res.data.refreshToken, res.data.user);
            const retried = req.clone({ setHeaders: { Authorization: `Bearer ${res.data.accessToken}` } });
            return next(retried);
          }),
          catchError(refreshError => {
            auth.logout();
            return throwError(() => refreshError);
          }),
        );
      }

      return throwError(() => error);
    }),
  );
};
