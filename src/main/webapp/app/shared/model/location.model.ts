export interface ILocation {
    id?: string;
    location?: string;
}

export class Location implements ILocation {
    constructor(public id?: string, public location?: string) {}
}
