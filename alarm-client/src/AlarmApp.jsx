import React, { Component } from 'react';
import AlarmStatus from './AlarmStatus.jsx';
import logo from './img/logo.svg';
import './css/AlarmApp.css';

class AlarmApp extends Component {
	
	render() {
		return (
	      <div className="App">
	        <header className="App-header">
			  <img src={logo} className="App-logo" alt="logo" />
	          <h1 className="App-title">Alarm application</h1>
	        </header>
	        <p className="App-intro">
	          Alarm application
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
