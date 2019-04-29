import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IIngredient } from 'app/shared/model/ingredient.model';
import { IngredientService } from './ingredient.service';

@Component({
    selector: 'jhi-ingredient-delete-dialog',
    templateUrl: './ingredient-delete-dialog.component.html'
})
export class IngredientDeleteDialogComponent {
    ingredient: IIngredient;

    constructor(
        protected ingredientService: IngredientService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.ingredientService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'ingredientListModification',
                content: 'Deleted an ingredient'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-ingredient-delete-popup',
    template: ''
})
export class IngredientDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ ingredient }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(IngredientDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.ingredient = ingredient;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/ingredient', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/ingredient', { outlets: { popup: null } }]);
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
