# AngularApp

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 8.0.1.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

Also `npm start` will start the server.

## Code scaffolding

Run `ng generate component component-name` (short: `ng g c component-name`) to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

### Adding a Component

`ng g c component-name` will generate component-name.component.ts.

### Adding a Service

`ng g s email` will generate email.service.ts.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Angular Data Binding

When the data in a Component changes, the HTML in the view is automatically update throught the binding.

### Data Binding with Property Binding

Binding angular fields to DOM properties is called Property Binding.

Use it when: ???
Limitations: It only works one-way, from the component towards the DOM. No data can be transfered from the DOM to the component using Property Binding! When the component changes, Angular will automatically update the DOM. Changes in the DOM will not make it into the component!

template:

```
<h1 [textContent]="title"></h1>
```

textContent is a DOM property (https://developer.mozilla.org/de/docs/Web/API/Node/textContent)

component:

```
export class CoursesComponent {
    title = "some title";
}
```

### Data Binding with String Interpolation

Use it when: you want to display dynamic text in headings, divs, spans, ...

template:

```
<h1>{{ title }}</h1>
```

component:

```
export class CoursesComponent {
    title = "some title";
}
```

Inside the brackets, you can also add javascript or typescript code.

## Using FontAwesome

Find icons here: https://glyphsearch.com/

https://github.com/FortAwesome/angular-fontawesome

Using NPM

```
$ npm install @fortawesome/fontawesome-svg-core
$ npm install @fortawesome/free-solid-svg-icons
```

See Compatibility table below to choose a correct version (I used 0.9.x)

```
$ npm install @fortawesome/angular-fontawesome@<version>
```

### Compatibility table

```
@fortawesome/angular-fontawesome    Angular      ng-add
0.1.x                               5.x          not supported
0.2.x                               6.x          not supported
0.3.x                               6.x && 7.x   not supported
0.4.x                               0.5.x 8.x    not supported
0.6.x                               9.x          supported
0.7.x                               10.x         supported
0.8.x                               11.x         supported
0.9.x                               12.x         supported
```

### Usage

To get up and running using Font Awesome with Angular follow below steps:

Add FontAwesomeModule to imports in src/app/app.module.ts:

```
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  imports: [
    BrowserModule,
    FontAwesomeModule
  ],
  declarations: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

Tie the icon to the property in your component src/app/app.component.ts:

```
import { Component } from '@angular/core';
import { faCoffee } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  faCoffee = faCoffee;
}
```

Use the icon in the template src/app/app.component.html:

```
<fa-icon [icon]="faCoffee"></fa-icon>
```
