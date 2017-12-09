import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import AlarmApp from './AlarmApp';
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(<AlarmApp />, document.getElementById('root'));
registerServiceWorker();
