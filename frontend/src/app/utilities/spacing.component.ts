import {Component} from '@angular/core';

@Component({
    templateUrl: './spacing.component.html',
    styles: [`i:not([class~="pi"]) {
        background-color: var(--surface-b);
        color: var(--primary-color);
        font-family: Monaco, courier, monospace;
        font-style: normal;
        font-size: 12px;
        padding: 2px 4px;
        letter-spacing: .5px;
        border-radius: 3px;
        font-weight: 600;
        margin: 0 2px;
    }`]
})
export class SpacingComponent {}
