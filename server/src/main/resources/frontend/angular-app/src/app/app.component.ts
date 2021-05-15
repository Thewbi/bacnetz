import { Component } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

/**
 * nvm
 * nvm current
 * nvm install --latest-npm
 * nvm ls
 *
 * // checking software versions
 * nvm current // shows node version
 * npm -v // shows npm version
 * node -v // shows node version
 * ng version
 *
 * npm install --save-dev @angular/cli@latest
 * npm i
 * npm start
 *
 * // update angular to the latest version
 * npm install -g @angular/cli@latest
 * ng update @angular/cli
 * ng update @angular/core @angular/cli
 *
 * ng add @ngrx/store
 */
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  title = 'BACnet Simulator';

  constructor(private http: HttpClient) { }

  onClickMe() {

    console.log('click');

    // get without parameters
    this.http.get<string>('http://192.168.0.234:8182/bacnetz/api/device/all')
    .subscribe(
       res => console.log('HTTP response', JSON.stringify(res)),
       err => console.log('HTTP Error', err),
       () => console.log('complete')
    );

    //// get with URL params
    //let params = new HttpParams().set('logNamespace', 'logNamespace');
    //
    //this.http.get<string>('http://127.0.0.1:8182/bacnetz/api/sysinfo/version', {params: params})
    //.subscribe(
    //    res => console.log('HTTP response', res),
    //    err => console.log('HTTP Error', err),
    //    () => console.log('complete')
    //);

    //let urlSearchParams = new URLSearchParams();
    //urlSearchParams.append('uid', '101');
    //const httpOptions = {
    //    params: { uid: 101 }
    //};

    // post with body
    // this.http.post('http://127.0.0.1:8182/bacnetz/api/device/toggle/', JSON.stringify({
    //   username: 'username',
    //   password: 'password',
    // })).subscribe(
    //     res => console.log('HTTP response', res),
    //     err => console.log('HTTP Error', err),
    //     () => console.log('complete')
    // );

    // post with body and URL params
    // this.http.post('http://127.0.0.1:8182/bacnetz/api/device/toggle', JSON.stringify({
    //   username: 'username',
    //   password: 'password',
    // }), {params: params}).subscribe(
    //     res => console.log('HTTP response', res),
    //     err => console.log('HTTP Error', err),
    //     () => console.log('complete')
    // );

    //// post with path parameter
    //const url = 'http://127.0.0.1:8182/bacnetz/api/device/toggle/' + 101;
    //this.http.post(url, {}).subscribe(
    //    res => console.log('HTTP response', res),
    //    err => console.log('HTTP Error', err),
    //    () => console.log('complete')
    //);

    // post
    //const url = 'http://127.0.0.1:8182/bacnetz/api/device/toggle';
    const url = 'http://192.168.0.234:8182/bacnetz/api/device/toggle';
    this.http.post(url, {}).subscribe(
        res => console.log('HTTP response', res),
        err => console.log('HTTP Error', err),
        () => console.log('complete')
    );
  }
}
