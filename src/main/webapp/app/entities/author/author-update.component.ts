import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { IAuthor } from 'app/shared/model/author.model';
import { AuthorService } from './author.service';
import { ILocation } from 'app/shared/model/location.model';
import { LocationService } from 'app/entities/location';
import { IRole } from 'app/shared/model/role.model';
import { RoleService } from 'app/entities/role';

@Component({
    selector: 'jhi-author-update',
    templateUrl: './author-update.component.html'
})
export class AuthorUpdateComponent implements OnInit {
    author: IAuthor;
    isSaving: boolean;

    users: ILocation[];

    roles: IRole[];
    created: string;
    lastActive: string;
    birthday: string;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected authorService: AuthorService,
        protected locationService: LocationService,
        protected roleService: RoleService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ author }) => {
            this.author = author;
            this.created = this.author.created != null ? this.author.created.format(DATE_TIME_FORMAT) : null;
            this.lastActive = this.author.lastActive != null ? this.author.lastActive.format(DATE_TIME_FORMAT) : null;
            this.birthday = this.author.birthday != null ? this.author.birthday.format(DATE_TIME_FORMAT) : null;
        });
        this.locationService
            .query({ filter: 'author-is-null' })
            .pipe(
                filter((mayBeOk: HttpResponse<ILocation[]>) => mayBeOk.ok),
                map((response: HttpResponse<ILocation[]>) => response.body)
            )
            .subscribe(
                (res: ILocation[]) => {
                    if (!this.author.user || !this.author.user.id) {
                        this.users = res;
                    } else {
                        this.locationService
                            .find(this.author.user.id)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<ILocation>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<ILocation>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: ILocation) => (this.users = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
        this.roleService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IRole[]>) => mayBeOk.ok),
                map((response: HttpResponse<IRole[]>) => response.body)
            )
            .subscribe((res: IRole[]) => (this.roles = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.author.created = this.created != null ? moment(this.created, DATE_TIME_FORMAT) : null;
        this.author.lastActive = this.lastActive != null ? moment(this.lastActive, DATE_TIME_FORMAT) : null;
        this.author.birthday = this.birthday != null ? moment(this.birthday, DATE_TIME_FORMAT) : null;
        if (this.author.id !== undefined) {
            this.subscribeToSaveResponse(this.authorService.update(this.author));
        } else {
            this.subscribeToSaveResponse(this.authorService.create(this.author));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IAuthor>>) {
        result.subscribe((res: HttpResponse<IAuthor>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackLocationById(index: number, item: ILocation) {
        return item.id;
    }

    trackRoleById(index: number, item: IRole) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}
