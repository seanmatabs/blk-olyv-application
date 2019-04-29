import { IRecipe } from 'app/shared/model/recipe.model';

export interface ICategory {
    id?: string;
    name?: string;
    recipes?: IRecipe[];
}

export class Category implements ICategory {
    constructor(public id?: string, public name?: string, public recipes?: IRecipe[]) {}
}
