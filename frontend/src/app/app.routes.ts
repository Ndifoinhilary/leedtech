import { Routes } from '@angular/router';
import { FeePaymentPageComponent } from './fee-payment-page.component';

export const routes: Routes = [
  { path: '', component: FeePaymentPageComponent },
  { path: 'one-time-fee-payment', component: FeePaymentPageComponent },
  { path: '**', redirectTo: '' }
];
