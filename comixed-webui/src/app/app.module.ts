import { providePrimeNG } from 'primeng/config';
import { NgModule } from '@angular/core';
import Aura from '@primeuix/themes/aura';

@NgModule({
  exports: [],
  imports: [],
  providers: [providePrimeNG({ theme: { preset: Aura } })]
})
export class AppModule {}
