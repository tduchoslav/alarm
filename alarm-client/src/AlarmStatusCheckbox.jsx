import React, { Component } from 'react';
import Switch from 'react-toggle-switch';
import "../node_modules/react-toggle-switch/dist/css/switch.min.css";

class AlarmStatusCheckbox extends Component {
    
    render() {
        console.debug('checkbox render: ' + this.props.on);
        return (
                <Switch onClick={this.props.handleStatusCheckboxChange} on={this.props.on}>
                </Switch>
                );                
    }
}

export default AlarmStatusCheckbox;
