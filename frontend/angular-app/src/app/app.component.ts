import { Component } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError, retry } from "rxjs/operators";
import { faCoffee, faDigitalTachograph } from '@fortawesome/free-solid-svg-icons';

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
 * npm i -g npm-check-updatesncu -u
 * ncu -u
 * npm install
 * // whenever there is error output, adjust the versions in your local package.json accordingly, until the errors are gone
 *
 * npm install -g @angular/cli@latest
 * ng update @angular/cli
 * ng update @angular/core @angular/cli
 *
 * // install ngrx
 * // delete the browserslist file from the project!
 * // update the angular cli version in the file angular-cli.json within the project! This file somehow fixes the ng version!
 * npm install @ngrx/store --save
 * ng add @ngrx/store@latest
 * ng add @ngrx/store-devtools
 *
 * npm cache clean --force
 * npm cache verify
 *
 * npm start
 *
 * http://127.0.0.1:4200/bacnetz/index.html
 */
@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent {

  title = "BACnetz Simulator";

  faCoffee = faCoffee;

  faDigitalTachograph = faDigitalTachograph;

  apiUrl = "/bacnetz/api";

  //websocketUrl = "ws://192.168.0.234:8182/bacnetz/push";
  //websocketUrl = "ws://127.0.0.1:8182/bacnetz/push";
  //websocketUrl = "ws://192.168.0.11:8182/bacnetz/push";
  websocketUrl = "ws:///bacnetz/push";

  constructor(private http: HttpClient) {}

  onClickMe() {

    console.log("click");

    let ws = new WebSocket(this.websocketUrl);

    console.log("ws");

    ws.onopen = function () {
      console.log("onOpen");

      //Subscribe to the channel
      ws.send(
        JSON.stringify({
          command: "subscribe",
          identifier: '{"channel":"ArticlesChannel"}',
        })
      );
    };

    ws.onmessage = function (msg) {
      console.log(msg);
    };

    // get without parameters
    this.http
      .get<string>(this.apiUrl + "/device/all")
      .subscribe(
        (res) => console.log("HTTP response", JSON.stringify(res)),
        (err) => console.log("HTTP Error", err),
        () => console.log("complete")
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
    //const url = "http://192.168.0.234:8182/bacnetz/api/device/toggle";
    this.http.post(this.apiUrl + "/device/toggle", {}).subscribe(
      (res) => console.log("HTTP response", res),
      (err) => console.log("HTTP Error", err),
      () => console.log("complete")
    );
  }
}
