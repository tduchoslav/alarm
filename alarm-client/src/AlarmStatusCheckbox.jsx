import React, { Component } from 'react';

class AlarmStatusCheckbox extends Component {
    
    render() {
        console.info('checkbox render: ' + this.props.on);
        return (<input type="checkbox" ref="statusCheckBox" name="statusCheckbox" checked={this.props.on} onChange={this.props.handleStatusCheckboxChange}/>);                
    }
}

export default AlarmStatusCheckbox;
