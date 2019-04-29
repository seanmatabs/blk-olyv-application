import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IIngredient } from 'app/shared/model/ingredient.model';
import { IngredientService } from './ingredient.service';

@Component({
    selector: 'jhi-ingredient-update',
    templateUrl: './ingredient-update.component.html'
})
export class IngredientUpdateComponent implements OnInit {
    ingredient: IIngredient;
    isSaving: boolean;

    constructor(protected ingredientService: IngredientService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ ingredient }) => {
            this.ingredient = ingredient;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.ingredient.id !== undefined) {
            this.subscribeToSaveResponse(this.ingredientService.update(this.ingredient));
        } else {
            this.subscribeToSaveResponse(this.ingredientService.create(this.ingredient));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IIngredient>>) {
        result.subscribe((res: HttpResponse<IIngredient>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
