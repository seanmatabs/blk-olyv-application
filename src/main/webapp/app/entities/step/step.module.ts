import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BlkOlyvAppSharedModule } from 'app/shared';
import {
    StepComponent,
    StepDetailComponent,
    StepUpdateComponent,
    StepDeletePopupComponent,
    StepDeleteDialogComponent,
    stepRoute,
    stepPopupRoute
} from './';

const ENTITY_STATES = [...stepRoute, ...stepPopupRoute];

@NgModule({
    imports: [BlkOlyvAppSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [StepComponent, StepDetailComponent, StepUpdateComponent, StepDeleteDialogComponent, StepDeletePopupComponent],
    entryComponents: [StepComponent, StepUpdateComponent, StepDeleteDialogComponent, StepDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BlkOlyvAppStepModule {}
