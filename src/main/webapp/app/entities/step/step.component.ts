import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IStep } from 'app/shared/model/step.model';
import { AccountService } from 'app/core';
import { StepService } from './step.service';

@Component({
    selector: 'jhi-step',
    templateUrl: './step.component.html'
})
export class StepComponent implements OnInit, OnDestroy {
    steps: IStep[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        protected stepService: StepService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected activatedRoute: ActivatedRoute,
        protected accountService: AccountService
    ) {
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.stepService
                .search({
                    query: this.currentSearch
                })
                .pipe(
                    filter((res: HttpResponse<IStep[]>) => res.ok),
                    map((res: HttpResponse<IStep[]>) => res.body)
                )
                .subscribe((res: IStep[]) => (this.steps = res), (res: HttpErrorResponse) => this.onError(res.message));
            return;
        }
        this.stepService
            .query()
            .pipe(
                filter((res: HttpResponse<IStep[]>) => res.ok),
                map((res: HttpResponse<IStep[]>) => res.body)
            )
            .subscribe(
                (res: IStep[]) => {
                    this.steps = res;
                    this.currentSearch = '';
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.currentSearch = query;
        this.loadAll();
    }

    clear() {
        this.currentSearch = '';
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInSteps();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IStep) {
        return item.id;
    }

    registerChangeInSteps() {
        this.eventSubscriber = this.eventManager.subscribe('stepListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
