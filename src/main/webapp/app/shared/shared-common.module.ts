import { NgModule } from '@angular/core';

import { BlkOlyvAppSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [BlkOlyvAppSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [BlkOlyvAppSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class BlkOlyvAppSharedCommonModule {}
