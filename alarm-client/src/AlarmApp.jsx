import React, { Component } from 'react';
import AlarmStatus from './AlarmStatus.jsx';
import logo from './logo.svg';
import './AlarmApp.css';

class AlarmApp extends Component {
	
	render() {
		return (
	      <div className="App">
	        <header className="App-header">
	          <img src={logo} className="App-logo" alt="logo" />
	          <h1 className="App-title">Welcome to React</h1>
	        </header>
	        <p className="App-intro">
	          To get started, edit <code>src/AlarmApp.js</code> and save to reload.
	        </p>
	          <table>
	              <tbody>
	              <AlarmStatus />
	              </tbody>
	          </table>
	      </div>
		);
	}
}

export default AlarmApp;
