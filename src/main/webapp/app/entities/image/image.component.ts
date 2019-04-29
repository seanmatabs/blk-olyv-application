import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IImage } from 'app/shared/model/image.model';
import { AccountService } from 'app/core';
import { ImageService } from './image.service';

@Component({
    selector: 'jhi-image',
    templateUrl: './image.component.html'
})
export class ImageComponent implements OnInit, OnDestroy {
    images: IImage[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        protected imageService: ImageService,
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
            this.imageService
                .search({
                    query: this.currentSearch
                })
                .pipe(
                    filter((res: HttpResponse<IImage[]>) => res.ok),
                    map((res: HttpResponse<IImage[]>) => res.body)
                )
                .subscribe((res: IImage[]) => (this.images = res), (res: HttpErrorResponse) => this.onError(res.message));
            return;
        }
        this.imageService
            .query()
            .pipe(
                filter((res: HttpResponse<IImage[]>) => res.ok),
                map((res: HttpResponse<IImage[]>) => res.body)
            )
            .subscribe(
                (res: IImage[]) => {
                    this.images = res;
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
        this.registerChangeInImages();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IImage) {
        return item.id;
    }

    registerChangeInImages() {
        this.eventSubscriber = this.eventManager.subscribe('imageListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
