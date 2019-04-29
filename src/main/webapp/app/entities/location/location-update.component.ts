import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ILocation } from 'app/shared/model/location.model';
import { LocationService } from './location.service';

@Component({
    selector: 'jhi-location-update',
    templateUrl: './location-update.component.html'
})
export class LocationUpdateComponent implements OnInit {
    location: ILocation;
    isSaving: boolean;

    constructor(protected locationService: LocationService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ location }) => {
            this.location = location;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.location.id !== undefined) {
            this.subscribeToSaveResponse(this.locationService.update(this.location));
        } else {
            this.subscribeToSaveResponse(this.locationService.create(this.location));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ILocation>>) {
        result.subscribe((res: HttpResponse<ILocation>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
