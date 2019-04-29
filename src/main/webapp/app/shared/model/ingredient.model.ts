export interface IIngredient {
    id?: string;
    name?: string;
    unit?: string;
}

export class Ingredient implements IIngredient {
    constructor(public id?: string, public name?: string, public unit?: string) {}
}
