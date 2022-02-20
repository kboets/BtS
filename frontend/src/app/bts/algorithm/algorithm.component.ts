import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AlgorithmService} from "./algorithm.service";
import {Observable} from "rxjs";
import * as _ from 'underscore';
import {Algorithm} from "../domain/algorithm";
@Component({
    selector: 'bts-algorithm',
    templateUrl: './algorithm.component.html'
})
export class AlgorithmComponent implements OnInit {

    public algorithm;
    public addAlgorithm: boolean;
    newAlgorithm: Algorithm;
    submitted = false;
    addAlgorithmForm: FormGroup;

    allAlgorithms$: Observable<Algorithm[]>;

    constructor(private fb: FormBuilder, private algorithmService: AlgorithmService) {
        this.addAlgorithm = false;
        this.newAlgorithm = {} as Algorithm;
    }
    ngOnInit(): void {
        this.createAddForm();
        this.allAlgorithms$=this.algorithmService.getAlgorithms()

        this.algorithmService.getAlgorithms()
            .subscribe((data) => {
                this.algorithm = _.chain(data).filter( function (algorithm){
                    return algorithm.current === true;
                }).first(1)
            });

    }

    private createAddForm() {
        this.addAlgorithmForm = this.fb.group({
            homeWin: 0,
            homeDraw: 0,
            homeLost: 0,
            homeBonus: 0,
            awayWin: 0,
            awayDraw: 0,
            awayLost: 0,
            awayMalus: 0,
            name: ['', Validators.required],
            current: {value: false, disabled: false},
            threshold: 0
        })
    }

    get addForm() { return this.addAlgorithmForm.controls; }

    onAddSubmit() {
        this.submitted = true;
        if (this.addAlgorithmForm.invalid) {
            return;
        }
        this.submitted = Object.assign(this.newAlgorithm, this.addAlgorithmForm.value)
        console.log(this.newAlgorithm);

    }

    onAddReset() {
        this.submitted = false;
        this.createAddForm();
    }

    public openAddForm() {
        this.addAlgorithm = true;
    }




}
