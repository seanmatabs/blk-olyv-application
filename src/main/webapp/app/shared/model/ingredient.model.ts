import { IRecipe } from 'app/shared/model/recipe.model';

export interface IIngredient {
    id?: string;
    name?: string;
    unit?: string;
    recipes?: IRecipe[];
}

export class Ingredient implements IIngredient {
    constructor(public id?: string, public name?: string, public unit?: string, public recipes?: IRecipe[]) {}
}
