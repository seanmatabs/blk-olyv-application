import { Moment } from 'moment';
import { ILocation } from 'app/shared/model/location.model';
import { IRole } from 'app/shared/model/role.model';

export interface IAuthor {
    id?: string;
    imageUrl?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    phoneNumber?: string;
    created?: Moment;
    lastActive?: Moment;
    password?: string;
    birthday?: Moment;
    location?: ILocation;
    roles?: IRole[];
}

export class Author implements IAuthor {
    constructor(
        public id?: string,
        public imageUrl?: string,
        public firstName?: string,
        public lastName?: string,
        public email?: string,
        public phoneNumber?: string,
        public created?: Moment,
        public lastActive?: Moment,
        public password?: string,
        public birthday?: Moment,
        public location?: ILocation,
        public roles?: IRole[]
    ) {}
}
