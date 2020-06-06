import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {PageHeaderModule} from "../../shared/modules";
import {LeagueComponent} from "./league.component";
import {LeagueRoutingModule} from "./league-routing.module";
import {FormsModule} from "@angular/forms";
import { MatCheckboxModule } from '@angular/material/checkbox';

@NgModule({
    imports: [CommonModule, LeagueRoutingModule, PageHeaderModule, FormsModule, MatCheckboxModule],
    declarations: [LeagueComponent]
})
export class LeagueModule{}
