import { IAuthor } from 'app/shared/model/author.model';

export interface IRole {
    id?: string;
    title?: string;
    description?: string;
    authors?: IAuthor[];
}

export class Role implements IRole {
    constructor(public id?: string, public title?: string, public description?: string, public authors?: IAuthor[]) {}
}
