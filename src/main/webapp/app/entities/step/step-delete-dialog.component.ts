import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IStep } from 'app/shared/model/step.model';
import { StepService } from './step.service';

@Component({
    selector: 'jhi-step-delete-dialog',
    templateUrl: './step-delete-dialog.component.html'
})
export class StepDeleteDialogComponent {
    step: IStep;

    constructor(protected stepService: StepService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.stepService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'stepListModification',
                content: 'Deleted an step'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-step-delete-popup',
    template: ''
})
export class StepDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ step }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(StepDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.step = step;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/step', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/step', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
