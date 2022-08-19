import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AlgorithmService} from "./algorithm.service";
import {EMPTY, Subject} from "rxjs";
import {Algorithm} from "../domain/algorithm";
import {GeneralError} from "../domain/generalError";
import {catchError, debounceTime} from "rxjs/operators";

@Component({
    selector: 'bts-algorithm',
    templateUrl: './algorithm.component.html'
})
export class AlgorithmComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    private allAlgorithms: Algorithm[];
    public currentAlgorithm: Algorithm;
    public addAlgorithm: boolean;
    newAlgorithm: Algorithm;
    submitted = false;
    addAlgorithmForm: FormGroup;
    confirmationMessage: string;
    isSavedOK: boolean;
    isEmptyAlgorithme: boolean;

    constructor(private fb: FormBuilder, private algorithmService: AlgorithmService) {
        this.addAlgorithm = false;
        this.newAlgorithm = {} as Algorithm;
        this.confirmationMessage = 'Het algoritme werd opgeslagen en is nu de standaard';
        this.isSavedOK = false;
        this.isEmptyAlgorithme = true;
        this.allAlgorithms = [];
    }

    ngOnInit(): void {
        this.createAddForm();
        this.retrieveCurrentAlgorithm();
        //this.retrieveAllAlgorithm();

        this.algorithmService.algorithmRefreshAction$
            .subscribe(
                console.log('refresh action called'),
                this.retrieveAllAlgorithm
            );
    }

    private retrieveCurrentAlgorithm() {
        this.algorithmService.getCurrentAlgorithme()
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        ).subscribe((data) => {
            this.currentAlgorithm = data;
            if (data !== null) {
                this.isEmptyAlgorithme = false;
            }
        })
    }



    private retrieveAllAlgorithm() {
        this.algorithmService.getAlgorithms()
            .pipe(debounceTime(1000))
            .pipe(catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((data) => {
                this.allAlgorithms = data;
            })
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
            current: {value: false},
            threshold: 0
        })
    }

    get addForm() { return this.addAlgorithmForm.controls; }

    onAddSubmit() {
        this.submitted = true;
        if (this.addAlgorithmForm.invalid) {
            return;
        }
        this.setAlgorithmValues();

        if (this.verifyAlreadyExisting() === true) {
            let dataError = new GeneralError();
            dataError.userFriendlyMessage = 'Dit algoritme bestaat reeds.'
            this.errorMessageSubject.next(dataError);
        } else {
            this.saveAlgorithm();
        }
    }

    private saveAlgorithm() {
        this.algorithmService.saveAlgorithm(this.newAlgorithm)
            .subscribe((data) => {
                this.algorithmService.algorithmRefreshNeeded$.next(data);
                this.retrieveCurrentAlgorithm();
                this.removeAcknowledgeMessage();
                this.addAlgorithm = false;
                this.isSavedOK = true;
                this.isEmptyAlgorithme = false;
            })
    }

    private verifyAlreadyExisting(): boolean {
        this.allAlgorithms.forEach(algoritm => {
            if (this.newAlgorithm.homeBonus === algoritm.homeBonus
                && this.newAlgorithm.awayMalus === algoritm.awayMalus
                && this.newAlgorithm.awayPoints.win === algoritm.awayPoints.win
                && this.newAlgorithm.awayPoints.lose === algoritm.awayPoints.lose
                && this.newAlgorithm.awayPoints.draw === algoritm.awayPoints.draw
                && this.newAlgorithm.homePoints.win === algoritm.homePoints.win
                && this.newAlgorithm.homePoints.lose === algoritm.homePoints.lose
                && this.newAlgorithm.homePoints.draw === algoritm.homePoints.draw
            ) {
                return true;
            }
        })
        return false;
    }

    onAddReset() {
        this.submitted = false;
        this.createAddForm();
    }

    openAddForm() {
        this.addAlgorithm = true;
    }

    private setAlgorithmValues() {
        this.newAlgorithm = {} as Algorithm;
        this.newAlgorithm.homePoints = {
            'win' : this.addAlgorithmForm.get('homeWin').value,
            'draw' : this.addAlgorithmForm.get('homeDraw').value,
            'lose' : this.addAlgorithmForm.get('homeLost').value
        }
        this.newAlgorithm.awayPoints = {
            'win' : this.addAlgorithmForm.get('awayWin').value,
            'draw' : this.addAlgorithmForm.get('awayDraw').value,
            'lose' : this.addAlgorithmForm.get('awayLost').value
        }
        this.newAlgorithm.name = this.addAlgorithmForm.get('name').value;
        this.newAlgorithm.threshold = this.addAlgorithmForm.get('threshold').value;
        if (this.addAlgorithmForm.get('current').value === true) {
            this.newAlgorithm.current = true;
        } else {
            this.newAlgorithm.current = true;
        }
        this.newAlgorithm.type = 'WIN';
        this.newAlgorithm.homeBonus = this.addAlgorithmForm.get('homeBonus').value;
        this.newAlgorithm.awayMalus = this.addAlgorithmForm.get('awayMalus').value;
        console.log('new algortime ', this.newAlgorithm);

    }


    private removeAcknowledgeMessage() {
        setTimeout(() => {
            this.isSavedOK = false;
        }, 5000);
    }

}
